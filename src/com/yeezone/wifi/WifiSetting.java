package com.yeezone.wifi;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.yeezone.aijiuyi.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WifiSetting extends SettingsPreferenceFragment implements DialogInterface.OnClickListener{

    private static final int WIFI_DIALOG_ID = 1;
    private static final int WIFI_SKIPPED_DIALOG_ID = 4;
    private static final int WIFI_AND_MOBILE_SKIPPED_DIALOG_ID = 5;
    
    private static final String SAVE_DIALOG_EDIT_MODE = "edit_mode";
    private static final String SAVE_DIALOG_ACCESS_POINT_STATE = "wifi_ap_state";
	
	private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
	private String TAG = "WifiSetting";
	
    private final IntentFilter mFilter;
    private final BroadcastReceiver mReceiver;
    private final Scanner mScanner;
    
    private WifiManager mWifiManager;
    private TextView mEmptyView;
    private ListView mListView;
    
    private DetailedState mLastState;
    private WifiInfo mLastInfo;
    
    private PreferenceCategory nearbyWifiList;
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    
    public static final int INVALID_NETWORK_ID = -1;
    public static final int LAST_CONNECTED_AP_PRIORITY = 2 << 24;
    
    private AccessPoint mSelectedAccessPoint;
    
    private WifiDialog mDialog;
    private AccessPoint mDlgAccessPoint;
    private Bundle mAccessPointSavedState;
    
    public WifiSetting() {
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEvent(context, intent);
            }

        };

        mScanner = new Scanner();
    }
	
    private void addMessagePreference(int messageId) {
        if (mEmptyView != null) mEmptyView.setText(messageId);
        getPreferenceScreen().removeAll();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If the dialog is showing, save its state.
        if (mDialog != null && mDialog.isShowing()) {
            if (mDlgAccessPoint != null) {
                mAccessPointSavedState = new Bundle();
                mDlgAccessPoint.saveWifiState(mAccessPointSavedState);
                outState.putBundle(SAVE_DIALOG_ACCESS_POINT_STATE, mAccessPointSavedState);
            }
        }
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		addPreferencesFromResource(R.xml.wifi_settings);
        mEmptyView = (TextView) getView().findViewById(android.R.id.empty);
        mListView = ((ListView)getView().findViewById(android.R.id.list));
        mListView.setEmptyView(mEmptyView);
        mListView.setPadding(0, 0, 0, 0);
        mListView.setDivider(null);
        
        if (savedInstanceState != null
                && savedInstanceState.containsKey(SAVE_DIALOG_ACCESS_POINT_STATE)) {
            mAccessPointSavedState = savedInstanceState.getBundle(SAVE_DIALOG_ACCESS_POINT_STATE);
        }
        
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
        if (preference instanceof AccessPoint) {
            mSelectedAccessPoint = (AccessPoint) preference;
            
            if(mSelectedAccessPoint.networkId != INVALID_NETWORK_ID) {
            	if(mSelectedAccessPoint.getState() == DetailedState.CONNECTED)
            		connectedWifiDialog(mSelectedAccessPoint);
            	else
            		haveSavedWifiDialog(mSelectedAccessPoint);
            }else if (mSelectedAccessPoint.security == AccessPoint.SECURITY_NONE && !mSelectedAccessPoint.wpsAvailable &&
                mSelectedAccessPoint.networkId == INVALID_NETWORK_ID) {
            	noSaveNoPwdDialog(mSelectedAccessPoint);
        } else {
            showDialog(mSelectedAccessPoint, false);
        }
    } else {
        return super.onPreferenceTreeClick(screen, preference);
    }
        return true;
	}
	
    private void showDialog(AccessPoint accessPoint, boolean edit) {
        if (mDialog != null) {
            removeDialog(WIFI_DIALOG_ID);
            mDialog = null;
        }

        // Save the access point and edit mode
        mDlgAccessPoint = accessPoint;

        showDialog(WIFI_DIALOG_ID);
    }
    
    private WifiConfiguration isExsits(String SSID)    
    {    
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();    
           for (WifiConfiguration existingConfig : existingConfigs)     
           {    
             if (existingConfig.SSID.equals("\""+SSID+"\""))    
             {    
                 return existingConfig;    
             }    
           }    
        return null;     
    }  
    
    public int addWifiConfig(int s, String ssid, String pwd){
    	int wifiId = -1;
    	
		Log.i(TAG,"AddWifiConfig equals ssid:" + ssid);		
		WifiConfiguration wifiCong = new WifiConfiguration();
		wifiCong.allowedAuthAlgorithms.clear(); 
		wifiCong.allowedGroupCiphers.clear(); 
		wifiCong.allowedKeyManagement.clear(); 
		wifiCong.allowedPairwiseCiphers.clear(); 
		wifiCong.allowedProtocols.clear(); 
		wifiCong.SSID = "\""+ ssid +"\"";
		
		WifiConfiguration tempConfig = this.isExsits(ssid);             
        if(tempConfig != null) {    
            mWifiManager.removeNetwork(tempConfig.networkId);    
        }  
		
		if( pwd != null && !"".equals(pwd) ) {
            //这里比较关键，如果是WEP加密方式的网络，密码需要放到cfg.wepKeys[0]里面
            if(s == AccessPoint.SECURITY_WEP) {
            	wifiCong.wepKeys[0]   = "\"" + pwd + "\"";            	
            	wifiCong.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED); 
            	wifiCong.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
            	wifiCong.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP); 
            	wifiCong.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40); 
            	wifiCong.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104); 
            	wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
            	wifiCong.wepTxKeyIndex = 0; 
            	Log.i(TAG, "addWifiConfig password WEP");
            }
            else {
            	wifiCong.preSharedKey = "\"" + pwd + "\"";
            	wifiCong.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);   
            	wifiCong.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                         
            	wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                         
            	wifiCong.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                    
//            	wifiCong.allowedProtocols.set(WifiConfiguration.Protocol.WPA); 
            	wifiCong.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            	wifiCong.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);            	
            	wifiCong.status = WifiConfiguration.Status.ENABLED;
            	Log.i(TAG, "addWifiConfig password WPA");
            }                 
        }
		else
		{
			wifiCong.wepKeys[0] = "\"" + "\""; 
			wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
			wifiCong.wepTxKeyIndex = 0; 
			wifiCong.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			wifiCong.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			Log.i(TAG, "addWifiConfig no password");
		}
		wifiCong.hiddenSSID = false;
		wifiCong.status = WifiConfiguration.Status.ENABLED;
		Log.i(TAG, "addWifiConfig wifiCong :" + wifiCong);
		wifiId = mWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
		Log.i(TAG, "addWifiConfig wifiId :" + wifiId);
    	return wifiId;
    }
    
 	 private void noSaveNoPwdDialog(AccessPoint ap)
 	 {
		  new AlertDialog.Builder(getActivity())
         .setTitle(ap.ssid)
         .setPositiveButton(R.string.str_connect, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 //do something
            	 
             	int id = addWifiConfig(-1, mSelectedAccessPoint.ssid, null);
           	  if(id != -1)
           	  {
           		mWifiManager.enableNetwork(id, true);
           	  }
           	mScanner.resume();
             }
         })
         .setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 //do something
           	  dialog.dismiss();
             }
         })	         
         .setCancelable(false)
         .show();
 	 }
 	 
	  private void haveSavedWifiDialog(final AccessPoint ap)
	  {
		  new AlertDialog.Builder(getActivity())
        .setTitle(ap.ssid)
        .setPositiveButton(R.string.str_connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do something
            	mWifiManager.enableNetwork(ap.networkId, true);   
            	mScanner.forceScan();	
            }
        })
        .setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do something
          	  dialog.dismiss();
            }
        })
        .setNeutralButton(R.string.str_nosave,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do something
            	mWifiManager.removeNetwork(ap.networkId);
            	mScanner.forceScan();	
            }
        })
        .setCancelable(false)
        .show();
	  }
    
    private void connectedWifiDialog(final AccessPoint ap)
    {  

		  new AlertDialog.Builder(getActivity())
          .setTitle(ap.ssid)
          .setPositiveButton(R.string.str_disconnect, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  //do something
                  mWifiManager.disconnect();  
                  mScanner.forceScan();	
              }
          })
          .setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  //do something
            	  dialog.dismiss();
              }
          })
          .setNeutralButton(R.string.str_nosave,new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  //do something
            	  mWifiManager.removeNetwork(ap.networkId);
            	  mScanner.forceScan();	
              }
          })
          .setCancelable(false)
          .show();
	  }
    
    
    @Override
    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case WIFI_DIALOG_ID:
                AccessPoint ap = mDlgAccessPoint; // For manual launch
                if (ap == null) { // For re-launch from saved state
                    if (mAccessPointSavedState != null) {
                        ap = new AccessPoint(getActivity(), mAccessPointSavedState);
                        // For repeated orientation changes
                        mDlgAccessPoint = ap;
                    }
                }
                // If it's still null, fine, it's for Add Network
                mSelectedAccessPoint = ap;
                mDialog = new WifiDialog(getActivity(), this, ap);
                return mDialog;
            case WIFI_SKIPPED_DIALOG_ID:
                return new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.wifi_skipped_message)
                            .setCancelable(false)
                            .setNegativeButton(R.string.wifi_skip_anyway,
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().setResult(Activity.RESULT_CANCELED);
                                    getActivity().finish();
                                }
                            })
                            .setPositiveButton(R.string.wifi_dont_skip,
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                            .create();
            case WIFI_AND_MOBILE_SKIPPED_DIALOG_ID:
                return new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.wifi_and_mobile_skipped_message)
                            .setCancelable(false)
                            .setNegativeButton(R.string.wifi_skip_anyway,
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().setResult(Activity.RESULT_CANCELED);
                                    getActivity().finish();
                                }
                            })
                            .setPositiveButton(R.string.wifi_dont_skip,
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                            .create();

        }
        return super.onCreateDialog(dialogId);
    }
	
    private List<AccessPoint> constructAccessPoints() {
        ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
        /** Lookup table to more quickly update AccessPoints by only considering objects with the
         * correct SSID.  Maps SSID -> List of AccessPoints with the given SSID.  */
        Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

        final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                AccessPoint accessPoint = new AccessPoint(getActivity(), config);
                accessPoint.update(mLastInfo, mLastState);
                accessPoints.add(accessPoint);
                apMap.put(accessPoint.ssid, accessPoint);
                Log.d(TAG, "constructAccessPoints SSID:"+config.SSID+" priority: " + config.priority);
            }
        }

        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            Log.w(TAG,"begining of the scan results");
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID == null || result.SSID.length() == 0 ||
                        result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                
                boolean found = false;
                for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
                    if (accessPoint.update(result)) {
                        found = true;
                        Log.i(TAG,"config in result:"+result.toString());
                    }
                }
                if (!found) {
                    AccessPoint accessPoint = new AccessPoint(getActivity(), result);
                    accessPoints.add(accessPoint);
                    apMap.put(accessPoint.ssid, accessPoint);
                }
            }
        }

        // Pre-sort accessPoints to speed preference insertion
        Collections.sort(accessPoints);
        return accessPoints;
    }
    
    private void updateWifiState(int state) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }

        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                mScanner.resume();
                return; // not break, to avoid the call to pause() below

            case WifiManager.WIFI_STATE_ENABLING:
                addMessagePreference(R.string.wifi_starting);
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                addMessagePreference(R.string.wifi_empty_list_wifi_off);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                addMessagePreference(R.string.wifi_stopping);
                break;
        }

        mLastInfo = null;
        mLastState = null;
        mScanner.pause();
    }
    
    private void handleEvent(Context context, Intent intent) {
        Log.d(TAG, "handleEvent intent = " + intent);
        String action = intent.getAction();
        System.out.println(action);
        Log.d(TAG, "handleEvent action = " + action);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_STATE_CHANGED_ACTION, updateWifiState");
            updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                Log.d(TAG, "SCAN_RESULTS_AVAILABLE_ACTION, or CONFIGURED_NETWORKS_CHANGED_ACTION or LINK_CONFIGURATION_CHANGED_ACTION, updateAccessPoints");
                updateAccessPoints();
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            SupplicantState state = (SupplicantState) intent.getParcelableExtra(
                    WifiManager.EXTRA_NEW_STATE);
            int erroSupplicantNumber = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR,0);
            if (!mConnected.get()) {
                Log.d(TAG, "updateConnectionState");
                updateConnectionState(WifiInfo.getDetailedStateOf(state));
            }
            if (state == SupplicantState.DISCONNECTED && erroSupplicantNumber != WifiManager.ERROR_AUTHENTICATING) {
                Log.d(TAG, "DISCONNECTED");
                updateConnectionState(WifiInfo.getDetailedStateOf(state));
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "NETWORK_STATE_CHANGED_ACTION, updateAccessPoints");
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(
                    WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
            updateAccessPoints();
            updateConnectionState(info.getDetailedState());
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "RSSI_CHANGED_ACTION, updateConnectionState");
            updateConnectionState(null);
        } 
    }
    
    private void updateConnectionState(DetailedState state) {
        if (!mWifiManager.isWifiEnabled()) {
            mScanner.pause();
            return;
        }

        if (state == DetailedState.OBTAINING_IPADDR) {
            mScanner.pause();
        } else {
            mScanner.resume();
        }

        mLastInfo = mWifiManager.getConnectionInfo();
        if (state != null) {
            mLastState = state;
        }
        for (int i = getPreferenceScreen().getPreferenceCount() - 1; i >= 0; --i) {
            // Maybe there's a WifiConfigPreference
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof AccessPoint) {
                final AccessPoint accessPoint = (AccessPoint) preference;
                accessPoint.update(mLastInfo, mLastState);
                Log.d(TAG, "update preference accessPoint = " + accessPoint);
            }
        }
    }
    
    private void updateAccessPoints() {
        // Safeguard from some delayed event handling
        if (getActivity() == null) return;

        final int wifiState = mWifiManager.getWifiState();
        Log.d(TAG, "Enter updateAccessPoints wifiState = " + wifiState);

        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                Log.d(TAG, "=====WifiManager.WIFI_STATE_ENABLED=====");
                // AccessPoints are automatically sorted with TreeSet.
                final Collection<AccessPoint> accessPoints = constructAccessPoints();
                Log.d(TAG, "remove all preference");
                getPreferenceScreen().removeAll();
                if(accessPoints.size() == 0) {
                    addMessagePreference(R.string.wifi_empty_list_wifi_on);
                }
                PreferenceCategory pc = new NoTitlePreferenceCategroy(getActivity());
                getPreferenceScreen().addPreference(pc);
                for (AccessPoint accessPoint : accessPoints) {
                	if(accessPoint.getState() != null){
                		pc.addPreference(accessPoint);
                		accessPoints.remove(accessPoint);
                		break;
                	}
                }
                
                nearbyWifiList = new PreferenceCategory(getActivity());
                nearbyWifiList.setLayoutResource(R.layout.preference_categroy_text);
                nearbyWifiList.setTitle(R.string.wifi_list2);
                getPreferenceScreen().addPreference(nearbyWifiList);
                for (AccessPoint accessPoint : accessPoints) {
                	if(accessPoint.mRssi != Integer.MAX_VALUE)
                		nearbyWifiList.addPreference(accessPoint);
                }
                break;

            case WifiManager.WIFI_STATE_ENABLING:
                Log.d(TAG, "=====WifiManager.WIFI_STATE_ENABLING=====remove all preference");
                getPreferenceScreen().removeAll();
                break;

            case WifiManager.WIFI_STATE_DISABLING:
                Log.d(TAG, "=====WifiManager.WIFI_STATE_DISABLING=====addMessagePreference");
                addMessagePreference(R.string.wifi_stopping);
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                Log.d(TAG, "=====WifiManager.WIFI_STATE_DISABLED=====addMessagePreference");
                addMessagePreference(R.string.wifi_empty_list_wifi_off);
                break;
        }
    }
    
    
    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mReceiver, mFilter);
        if (mWifiManager.isWifiEnabled()){
            Log.d(TAG, "wifi enabled, update access point");
            updateAccessPoints();
            mScanner.forceScan();
        } else {
            updateAccessPoints();
        }
    }
    


	@Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "WifiSettings: onPause()");
        getActivity().unregisterReceiver(mReceiver);
        mScanner.pause();
    }
    
    private class Multimap<K,V> {
        private HashMap<K,List<V>> store = new HashMap<K,List<V>>();
        /** retrieve a non-null list of values with key K */
        List<V> getAll(K key) {
            List<V> values = store.get(key);
            return values != null ? values : Collections.<V>emptyList();
        }

        void put(K key, V val) {
            List<V> curVals = store.get(key);
            if (curVals == null) {
                curVals = new ArrayList<V>(3);
                store.put(key, curVals);
            }
            curVals.add(val);
        }
    }
	
    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                Activity activity = getActivity();
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_fail_to_scan,
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which == DialogInterface.BUTTON_POSITIVE){
			String pd = mDialog.getPassWord();
			if(TextUtils.isEmpty(pd))
				return;
			int id = addWifiConfig(mDlgAccessPoint.security, mDlgAccessPoint.ssid, pd);
			System.out.println("netId = " + id);
			if(id != INVALID_NETWORK_ID) {
				boolean b = mWifiManager.enableNetwork(id, true);
				System.out.println("enable = " + b);
			}
			mScanner.resume();
		} else if(which == DialogInterface.BUTTON_NEGATIVE){
			
		}
		dialog.dismiss();
	}
}
