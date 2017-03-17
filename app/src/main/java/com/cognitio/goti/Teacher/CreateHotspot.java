package com.cognitio.goti.Teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.cognitio.goti.Constants;
import com.cognitio.goti.MainActivity;
import com.cognitio.goti.R;
import com.cognitio.goti.ToastMessages;
import com.cognitio.goti.WifiApControl;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CreateHotspot extends AppCompatActivity{

    TextView text,status,connectedClientsTv,connectedClientsNumber,hotspotPassword;
    EditText hotspotName;
    Button create,refresh,start;
    WifiApControl wifiApControl;
    WifiManager mWifiManager;
    Timer t;
    ArrayList<String> finalList;
    String quizToPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hotspot);
        Intent intent = getIntent();
        if(intent.getExtras()!=null)
        quizToPlay=intent.getStringExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY);

        text = (TextView)findViewById(R.id.text);
        status = (TextView)findViewById(R.id.status);
        connectedClientsTv = (TextView)findViewById(R.id.connected_clients_tv);
        connectedClientsNumber = (TextView)findViewById(R.id.connected);
        hotspotName = (EditText)findViewById(R.id.hotspot_tv);
        hotspotPassword = (EditText)findViewById(R.id.hotspot_password_tv);
        create = (Button)findViewById(R.id.create_hotspot);
        refresh = (Button)findViewById(R.id.refresh);
        start = (Button)findViewById(R.id.start);
        mWifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
        wifiApControl= WifiApControl.getApControl(mWifiManager);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hotspotName.getText().toString().matches("")){
                    hotspotName.setError("Enter Hotspot Name");
                }
                else if(hotspotPassword.getText().toString().matches("")){
                    hotspotPassword.setError("Enter Password");
                }
                else if(hotspotPassword.getText().toString().length()<8){
                    hotspotPassword.setError("Minimum 8 Characters");
                }
                else{

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
                                text.setVisibility(View.GONE);
                                hotspotName.setVisibility(View.GONE);
                                hotspotPassword.setVisibility(View.GONE);
                                create.setVisibility(View.GONE);
                                status.setText("Created");
                                status.setVisibility(View.VISIBLE);
                                start.setVisibility(View.VISIBLE);
                                connectedClientsTv.setVisibility(View.VISIBLE);
                                connectedClientsNumber.setVisibility(View.VISIBLE);
//                                refresh.setVisibility(View.VISIBLE);
                                t = new Timer();
//Set the schedule function and rate
                                final int[] n = {0};
                                t.scheduleAtFixedRate(new TimerTask() {
                                    @Override
                                    public void run() {
                                        getClientList();

                                    }
                                }, 4000, 4000);
                            }
                        }
                    }, 10000);




                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getClientList();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getClientList();
                final ProgressDialog dialog = new ProgressDialog(CreateHotspot.this);
                dialog.setMessage("Please Wait...");
                dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if(finalList.size()==0){
                            start.setVisibility(View.INVISIBLE);
                            Toast.makeText(CreateHotspot.this,"No Clients Connected",Toast.LENGTH_LONG).show();
                        }
                        else{
                            t.cancel();
                            Intent intent = new Intent(CreateHotspot.this,Quiz.class);
                            intent.putExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY,quizToPlay);
                            intent.putExtra(Constants.INTENT_FINAL_LIST,finalList);
                            startActivity(intent);
                        }
                    }
                },3000);

            }
        });



    }

    public void getClientList() {
        wifiApControl.getClientList(CreateHotspot.this, true, 3000, new WifiApControl.FinishScanListener() {
            @Override
            public void onFinishScan(ArrayList<String> resultIPAddr) {
                Log.e("clients",resultIPAddr.size()+"");
                finalList = new ArrayList<String>(resultIPAddr);
                connectedClientsNumber.setText(resultIPAddr.size()+"");
                if(finalList.size()>0)
                    start.setVisibility(View.VISIBLE);
                else
                    start.setVisibility(View.INVISIBLE);
//
            }
        });
    }
}
