package com.cognitio.goti;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is use to handle all Hotspot related information.
 *
 *
 *
 */
public class WifiApControl {
    private static Method getWifiApState;
    private static Method isWifiApEnabled;
    private static Method setWifiApEnabled;
    private static Method getWifiApConfiguration;
    List<ScanResult> mResults;
    ListView mNetworksList;
    Context context;

    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";

    public static final int WIFI_AP_STATE_DISABLED = WifiManager.WIFI_STATE_DISABLED;
    public static final int WIFI_AP_STATE_DISABLING = WifiManager.WIFI_STATE_DISABLING;
    public static final int WIFI_AP_STATE_ENABLED = WifiManager.WIFI_STATE_ENABLED;
    public static final int WIFI_AP_STATE_ENABLING = WifiManager.WIFI_STATE_ENABLING;
    public static final int WIFI_AP_STATE_FAILED = WifiManager.WIFI_STATE_UNKNOWN;

    public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = WifiManager.EXTRA_PREVIOUS_WIFI_STATE;
    public static final String EXTRA_WIFI_AP_STATE = WifiManager.EXTRA_WIFI_STATE;

    static {
        // lookup methods and fields not defined publicly in the SDK.
        Class<?> cls = WifiManager.class;
        for (Method method : cls.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getWifiApState")) {
                getWifiApState = method;
            } else if (methodName.equals("isWifiApEnabled")) {
                isWifiApEnabled = method;
            } else if (methodName.equals("setWifiApEnabled")) {
                setWifiApEnabled = method;
            } else if (methodName.equals("getWifiApConfiguration")) {
                getWifiApConfiguration = method;
            }
        }
    }

    public static boolean isApSupported() {
        return (getWifiApState != null && isWifiApEnabled != null
                && setWifiApEnabled != null && getWifiApConfiguration != null);
    }

    private WifiManager mgr;

    private WifiApControl(WifiManager mgr, Context context) {
        this.mgr = mgr;
        this.context = context;
    }

    public static WifiApControl getApControl(WifiManager mgr,Context context) {
        if (!isApSupported())
            return null;
        return new WifiApControl(mgr,context);
    }

    public boolean isWifiApEnabled() {
        try {
            return (Boolean) isWifiApEnabled.invoke(mgr);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return false;
        }
    }

    public boolean istWifiEnabled() {
        if (!mgr.isWifiEnabled() ) {
            return false;
        }else{
            return true;
        }
    }
    public boolean setWifiDisabled(){
        if(istWifiEnabled()){
            mgr.setWifiEnabled(false);
            return true;
        }else{
            return false;
        }
    }
    public boolean setWifiEnabled(){
        if(!istWifiEnabled()){
            mgr.setWifiEnabled(true);
            return true;
        }else{
            return false;
        }
    }
    public void wifiEnableDialog()
    {
        if(!mgr.isWifiEnabled())
        {
            final ProgressDialog progDialog = new ProgressDialog(context);
            progDialog.setMessage("Turning Wifi ON");
            progDialog.setTitle("WiFi");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Turn on Wifi ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            mgr.setWifiEnabled(true);
                            progDialog.show();
                            new Thread() {
                                public void run() {
                                    try{
                                        while (!mgr.isWifiEnabled()) {

                                            sleep(100);}
                                        progDialog.dismiss();
                                    } catch (Exception e) {}

                                }
                            }.start();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context,MainActivity.class);
                            context.startActivity(intent);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    public int getWifiApState() {
        try {
            return (Integer) getWifiApState.invoke(mgr);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return -1;
        }
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            return (WifiConfiguration) getWifiApConfiguration.invoke(mgr);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return null;
        }
    }

    public boolean setWifiApEnabled(WifiConfiguration config, boolean enabled) {
        try {

            if (enabled) { // disable WiFi in any case
                mgr.setWifiEnabled(false);
            }

            return (Boolean) setWifiApEnabled.invoke(mgr, config, enabled);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return false;
        }
    }
public boolean disableWifiAp(boolean enabled) {
        try {



            return (Boolean) setWifiApEnabled.invoke(mgr,enabled);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return false;
        }
    }


//    public void showHotspotsList(ListView List){
//        if(mgr.isWifiEnabled()) {
//            mReceiver = new WifiReceiver();
//            scanNetworks();
////            mNetworksList = mNetworksList==null ? List : mNetworksList;
////            if(mResults!=null){
////                mAdapter = new ScanResultsAdapter(mContext,mResults);
////                mNetworksList.setAdapter(mAdapter);
////            }
//        }
//        else {mgr.setWifiEnabled(true);
//            showHotspotsList();
//        }
//    }
    public List<ScanResult> getHotspotsList(){

        if(mgr.isWifiEnabled()) {

            if(mgr.startScan()){
                return mgr.getScanResults();
            }

        }
        return null;
    }

//    public void scanNetworks() {
//        boolean scan = mgr.startScan();
//
//        if(scan) {
//            mResults = mgr.getScanResults();
//
//        } else
//            switch(mgr.getWifiState()) {
//                case WifiManager.WIFI_STATE_DISABLING:
//                    Log.e("scanNetworks","wifi disabling");
//                    break;
//                case WifiManager.WIFI_STATE_DISABLED:
//                    Log.e("scanNetworks","wifi disabled");
//                    break;
//                case WifiManager.WIFI_STATE_ENABLING:
//                    Log.e("scanNetworks","wifi enabling");
//                    break;
//                case WifiManager.WIFI_STATE_ENABLED:
//                    Log.e("scanNetworks", "wifi enabled");
//                    break;
//                case WifiManager.WIFI_STATE_UNKNOWN:
//                    Log.e("scanNetworks","wifi unknown state");
//                    break;
//            }
//
//    }
//    class WifiReceiver extends BroadcastReceiver {
//
//        public List<ScanResult> getResults() {
//            return mResults;
//        }
//
//        @Override
//        public void onReceive(Context c, Intent intent) {
//            mResults = mgr.getScanResults();
//            Log.e("onReceive",mResults.toString());
////            mAdapter = new ScanResultsAdapter(mContext, mResults);
////            mNetworksList.setAdapter(mAdapter);
////            mAdapter.notifyDataSetChanged();
//
//        }
//
//
//    }

    public interface FinishScanListener{
        void onFinishScan(ArrayList<String> resultIPAddr);
    }
    public void getClientList(final Context context, final boolean onlyReachables, final int reachableTimeout, final FinishScanListener finishListener) {

        Runnable runnable = new Runnable() {
            public void run() {

                BufferedReader br = null;
                final ArrayList<String> resultIPAddr = new ArrayList<String>();

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split(" +");

                        if ((splitted != null) && (splitted.length >= 4)) {
                            // Basic sanity check
                            String mac = splitted[3];

                            if (mac.matches("..:..:..:..:..:..")) {
                                boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);

                                if (!onlyReachables || isReachable) {
                                    resultIPAddr.add(splitted[0]);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().toString(), e.toString());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(this.getClass().toString(), e.getMessage());
                    }
                }

                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finishListener.onFinishScan(resultIPAddr);
                    }
                };
                mainHandler.post(myRunnable);
            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    public void setMobileDataEnabled(boolean enabled) {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != setMobileDataEnabledMethod)
            {
                setMobileDataEnabledMethod.invoke(telephonyService, enabled);
            }
        }
        catch (Exception ex)
        {
            Log.e("error", "Error setting mobile data state", ex);
        }
    }
    public boolean getMobileDataState()
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod)
            {
                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);

                return mobileDataEnabled;
            }
        }
        catch (Exception ex)
        {
            Log.e("error", "Error getting mobile data state", ex);
        }

        return false;
    }

    /**
     * check if  Mobile Data With SIM Enabled
     */
    public boolean isMobileDataEnabled() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method method = connectivityManager.getClass().getMethod("getMobileDataEnabled");
            return (Boolean)method.invoke(connectivityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public String getSecurityMode(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        final String[] modes = {"WPA", "EAP","WEP" };
        for (int i = modes.length - 1; i >= 0; i--) {
            if (cap.contains(modes[i])) {
                return modes[i];
            }
        }
        return "OPEN";
    }

}