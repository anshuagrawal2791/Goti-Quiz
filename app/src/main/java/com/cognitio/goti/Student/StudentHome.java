package com.cognitio.goti.Student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cognitio.goti.Adapters.AvailableWifiAdapter;
import com.cognitio.goti.MainActivity;
import com.cognitio.goti.Player;
import com.cognitio.goti.R;
import com.cognitio.goti.ServerClient;
import com.cognitio.goti.Teacher.CreateHotspot;
import com.cognitio.goti.WifiApControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class StudentHome extends AppCompatActivity {

    WifiApControl wifiApControl;
    WifiManager mWifiManager;
//    FileServerAsyncTask receiveTask;
    Send senTask;
    String server;
    EditText name;
    Button submit_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        name = (EditText)findViewById(R.id.name);
        submit_name = (Button)findViewById(R.id.submit_name);
        mWifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
        wifiApControl= WifiApControl.getApControl(mWifiManager,this);
//        wifiApControl.setMobileDataEnabled(false);


        submit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().matches("")){
                wifiApControl.getClientList(StudentHome.this, true, 3000, new WifiApControl.FinishScanListener() {
                    @Override
                    public void onFinishScan(ArrayList<String> resultIPAddr) {
                        Log.e("clients",resultIPAddr.size()+"");
                        if(resultIPAddr.size()>0)
                        for(int i=0;i<resultIPAddr.size();i++) {
                            server = resultIPAddr.get(i);
                            if(senTask!=null)
                                senTask.cancel(true);

                            JSONObject toSend = new JSONObject();
                            try {
                                toSend.put("method","add");
                                toSend.put("name",name.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            senTask=new Send(StudentHome.this, toSend.toString(), resultIPAddr.get(i)) ;
//
                            senTask.execute();
                        }
                        else{
                            Toast.makeText(StudentHome.this,"You don't seem to be connected to the hotspot",Toast.LENGTH_LONG).show();
                        }

//
                    }
                });

                }
            }
        });



    }
//public  class FileServerAsyncTask extends AsyncTask {
//
//    private Context context;
//    private TextView statusText;
//    private String text;
//    private String currentClientIP;
//    private ServerSocket serverSocket;
//
//    public FileServerAsyncTask(Context context) {
//        this.context = context;
//
//    }
//
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected void onPostExecute(Object o) {
//        super.onPostExecute(o);
//        if(text!=null) {
//            Log.e("response", text);
//            Toast.makeText(context,"You're added!",Toast.LENGTH_LONG).show();
//            context.startActivity(new Intent(context,Waiting.class));
//        }
////        new ServerClient.Send(context,"You are Added",currentClientIP).execute();
////        task=new CreateHotspot.FileServerAsyncTask(context);
////        task.execute();
//    }
//
//    /**
//     * Start activity that can handle the JPEG image
//     */
//
//
//
//    @Override
//    protected Object doInBackground(Object[] objects) {
//        try {
//
//            /**
//             * Create a server socket and wait for client connections. This
//             * call blocks until a connection is accepted from a client
//             */
//             serverSocket = new ServerSocket(8888);
//            serverSocket.setSoTimeout(2000);
//            Socket client = serverSocket.accept();
//
//
//            /**
//             * If this code is reached, a client has connected and transferred data
//             * Save the input stream from the client as a JPEG file
//             */
////                final File f = new File(Environment.getExternalStorageDirectory() + "/"
////                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
////                        + ".jpg");
//
////                File dirs = new File(f.getParent());
////                if (!dirs.exists())
////                    dirs.mkdirs();
////                f.createNewFile();
//            BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
//            StringBuilder total = new StringBuilder();
//            String line;
//            while ((line = r.readLine()) != null) {
//                total.append(line).append('\n');
//            }
//            text = total.toString();
////            players.add(new Player(text,client.getInetAddress()+""));
//            currentClientIP = client.getInetAddress().toString().substring(1);
//
////                InputStream inputstream = client.getInputStream();
////                copyFile(inputstream, new FileOutputStream(f));
//            serverSocket.close();
////                return f.getAbsolutePath();
//            return null;
//        } catch (IOException e) {
//            Log.e("error", e.toString());
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    // some code #3 (Write your code here to run in UI thread)
//                    Toast.makeText(context,"Looks like you're connected to the wrong hotspot!",Toast.LENGTH_LONG).show();
//
//                }
//            });
//
//            try {
//                serverSocket.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            return null;
//        }
//    }
//}
    public  class Send extends AsyncTask {
        String hello, host;
        Context context;
        Boolean approved=false;
        String response;

        public Send(Context context, String hello, String host) {
            this.hello = hello;
            this.host = host;
            this.context = context;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("sent","sent");
            if(approved){
            Toast.makeText(context,"You're added!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context,Waiting.class);
                intent.putExtra("server",host);
            context.startActivity(intent);}
            else{
                Toast.makeText(context,"you don't seem to be connected to the right hotspot",Toast.LENGTH_LONG).show();
            }

//            if(receiveTask!=null)
//                receiveTask.cancel(true);
//            receiveTask = new FileServerAsyncTask(StudentHome.this);
//            receiveTask.execute();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            int port = 8888;
            int len;
            Socket socket = new Socket();
            byte buf[] = new byte[1024];
            try {
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 100);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
//                OutputStream outputStream = socket.getOutputStream();
//                outputStream.write(hello.getBytes(Charset.forName("UTF-8")));


//                outputStream.close();
//                OutputStream os = socket.getOutputStream();
//                OutputStreamWriter osw = new OutputStreamWriter(os);
//                BufferedWriter bw = new BufferedWriter(osw);
//                bw.write(hello);
////                bw.flush();
//                outputStream.flush();
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(hello);
                bw.newLine();
                bw.flush();

                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                response = br.readLine();
                System.out.println("Message received from the server : " +response);
                if(response!=null)
                approved=true;
//            ContentResolver cr = context.getContentResolver();
//            InputStream inputStream = null;
//            inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
//            while ((len = inputStream.read(buf)) != -1) {
//                outputStream.write(buf, 0, len);
//            }
//                outputStream.close();
                is.close();
//                outputStream.close();

//            inputStream.close();
            }catch (SocketTimeoutException e){
                try {
                    socket.close();
                    Log.e("exception",e.toString());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            catch (FileNotFoundException e) {
                //catch logic
            } catch (IOException e) {
                //catch logic
            }


/**
 * Clean up any open sockets when done
 * transferring or if an exception occurred.
 */ finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            //catch logic
                        }
                    }
                }

            }
            return null;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        super.onBackPressed();
    }
}








