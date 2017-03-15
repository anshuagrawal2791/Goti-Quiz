package com.cognitio.goti.Teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cognitio.goti.MainActivity;
import com.cognitio.goti.R;
import com.cognitio.goti.WifiApControl;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CreateHotspot extends AppCompatActivity{

    TextView text,status,connectedClientsTv,connectedClientsNumber,hotspotPassword;
    EditText hotspotName;
    Button create,refresh;
    WifiApControl wifiApControl;
    WifiManager mWifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hotspot);
        text = (TextView)findViewById(R.id.text);
        status = (TextView)findViewById(R.id.status);
        connectedClientsTv = (TextView)findViewById(R.id.connected_clients_tv);
        connectedClientsNumber = (TextView)findViewById(R.id.connected);
        hotspotName = (EditText)findViewById(R.id.hotspot_tv);
        hotspotPassword = (EditText)findViewById(R.id.hotspot_password_tv);
        create = (Button)findViewById(R.id.create_hotspot);
        refresh = (Button)findViewById(R.id.refresh);
        mWifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
        wifiApControl= WifiApControl.getApControl(mWifiManager);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hotspotName.getText().toString().matches("")||hotspotPassword.getText().toString().matches("")){
                    create.setError("Enter Details");
                }else{

                    final WifiConfiguration wifiCon = new WifiConfiguration();
                    wifiCon.SSID = hotspotName.getText().toString();
                    wifiCon.preSharedKey = hotspotPassword.getText().toString();
                    wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                    wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

                    wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    wifiApControl.setWifiApEnabled(wifiCon,false);
                    final ProgressDialog dialog = new ProgressDialog(CreateHotspot.this);
                    dialog.setMessage("creating...");
                    dialog.show();


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            if(wifiApControl.setWifiApEnabled(wifiCon,true)){
                                text.setVisibility(View.INVISIBLE);
                                hotspotName.setVisibility(View.INVISIBLE);
                                hotspotPassword.setVisibility(View.INVISIBLE);
                                create.setVisibility(View.INVISIBLE);
                                status.setText("Created");
                                status.setVisibility(View.VISIBLE);
                                connectedClientsTv.setVisibility(View.VISIBLE);
                                connectedClientsNumber.setVisibility(View.VISIBLE);
                                refresh.setVisibility(View.VISIBLE);
                                final Timer t = new Timer();
//Set the schedule function and rate
                                final int[] n = {0};
                                t.scheduleAtFixedRate(new TimerTask() {
                                    @Override
                                    public void run() {
                                        wifiApControl.getClientList(CreateHotspot.this, true, 3000, new WifiApControl.FinishScanListener() {
                                            @Override
                                            public void onFinishScan(ArrayList<String> resultIPAddr) {
                                                Log.e("clients",resultIPAddr.size()+"");
                                                connectedClientsNumber.setText(resultIPAddr.size()+"");
                                                n[0]++;
                                                if(n[0] ==20)
                                                    t.cancel();
                                            }
                                        });

                                    }
                                }, 4000, 4000);
                            }
                        }
                    }, 10000);




                }
            }
        });

    }
}
