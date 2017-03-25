package com.cognitio.goti.Teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import com.cognitio.goti.Player;
import com.cognitio.goti.R;
import com.cognitio.goti.ServerClient;
import com.cognitio.goti.ToastMessages;
import com.cognitio.goti.WifiApControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

public class CreateHotspot extends AppCompatActivity{

    TextView text,status,connectedClientsTv,connectedClientsNumber,hotspotPassword;
    EditText hotspotName;
    Button create,refresh,start;
    WifiApControl wifiApControl;
    WifiManager mWifiManager;
    Timer t;
    ArrayList<String> finalList;
    String quizToPlay;
//    static TreeSet<Player> players;
    static HashMap<String,String> players;
    FileServerAsyncTask task;
    Boolean toExecuteTask=true;
//    ServerSocket serverSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hotspot);
        Intent intent = getIntent();
        if(intent.getExtras()!=null)
        quizToPlay=intent.getStringExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY);
        Log.e("quiztoplay",quizToPlay);

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
        wifiApControl= WifiApControl.getApControl(mWifiManager,this);
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

                                players = new HashMap<String, String>();

//                                task = new FileServerAsyncTask(CreateHotspot.this){
//                                    @Override
//                                    protected void onPostExecute(Object o) {
//                                        Log.e("players",players.toString());
////                                        task.execute();
//                                        new FileServerAsyncTask(CreateHotspot.this).execute();
//                                    }
//                                };
//                                task.execute();
                                task=new FileServerAsyncTask(CreateHotspot.this);
                                task.execute();


//                                try {
////                                    serverSocket = new ServerSocket(8888);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                new FileServerAsyncTask(CreateHotspot.this).execute();
//                                refresh.setVisibility(View.VISIBLE);
//                                t = new Timer();
////Set the schedule function and rate
//                                final int[] n = {0};
//                                t.scheduleAtFixedRate(new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        getClientList();
//
//                                    }
//                                }, 4000, 4000);
                            }
                        }
                    }, 10000);




                }
            }
        });
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getClientList();
//            }
//        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getClientList();
                if (players.keySet().size() == 0) {
//                            start.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreateHotspot.this, "No Clients Connected", Toast.LENGTH_LONG).show();
                } else {
                    final ProgressDialog dialog = new ProgressDialog(CreateHotspot.this);
                    dialog.setMessage("Please Wait...");
                    dialog.show();
                    for (Map.Entry<String, String> entry : players.entrySet()) {
                        new ServerClient.Send(CreateHotspot.this,"quiz_started",entry.getKey(),8888).execute();
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            task.cancel(true);
                            Intent intent = new Intent(CreateHotspot.this, Quiz.class);
                            intent.putExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY, quizToPlay);
                            intent.putExtra(Constants.INTENT_FINAL_LIST, players);
                            startActivity(intent);

                        }
                    }, 3000);

                }
            }
        });



    }

//    public void getClientList() {
//        wifiApControl.getClientList(CreateHotspot.this, true, 3000, new WifiApControl.FinishScanListener() {
//            @Override
//            public void onFinishScan(ArrayList<String> resultIPAddr) {
//                Log.e("clients",resultIPAddr.size()+"");
//                Log.e("players",players.toString());
//                finalList = new ArrayList<String>(resultIPAddr);
//                connectedClientsNumber.setText(resultIPAddr.size()+"");
//                if(finalList.size()>0)
//                    start.setVisibility(View.VISIBLE);
//                else
//                    start.setVisibility(View.INVISIBLE);
////
//            }
//        });
//    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(t!=null)
//        t.cancel();
    }




    public  class FileServerAsyncTask extends AsyncTask {

        private Context context;
        private TextView statusText;
        private String text;
        private String currentClientIP;
        private String responseMessage=" ";

        public FileServerAsyncTask(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            textview.setText("fetching...");
//            receive.setEnabled(false);

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("player",players.toString());
            connectedClientsNumber.setText(players.keySet().size()+"");
            new ServerClient.Send(context,responseMessage,currentClientIP,8888).execute();
            connectedClientsNumber.setText(players.keySet().size()+"");
            task=new FileServerAsyncTask(context);
            task.execute();
        }

        /**
         * Start activity that can handle the JPEG image
         */



        @Override
        protected Object doInBackground(Object[] objects) {
            try {

                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(8888);
                Socket client = serverSocket.accept();


                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 */
//                final File f = new File(Environment.getExternalStorageDirectory() + "/"
//                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
//                        + ".jpg");

//                File dirs = new File(f.getParent());
//                if (!dirs.exists())
//                    dirs.mkdirs();
//                f.createNewFile();
                BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }
                text = total.toString();
                JSONObject received = new JSONObject(text);
                if(received.get("method").equals("add"))
                {
                    String name = received.getString("name");
                    String ip = client.getInetAddress().toString().substring(1);
                    players.put(ip,name);
                    responseMessage="Successfully added!";
                }
                else if(received.get("method").equals("remove")){
                    String ip = client.getInetAddress().toString().substring(1);
                    players.remove(ip);
                    responseMessage = "Successfully removed!";
                }
//                    players.add(new Player(received.getString("name"),client.getInetAddress().toString().substring(1)));
                currentClientIP = client.getInetAddress().toString().substring(1);

//                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
//                return f.getAbsolutePath();
                return null;
            } catch (IOException e) {
                Log.e("error", e.getMessage());
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
