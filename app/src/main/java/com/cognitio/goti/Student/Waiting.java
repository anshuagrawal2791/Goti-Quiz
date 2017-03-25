package com.cognitio.goti.Student;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.cognitio.goti.R;
import com.cognitio.goti.ServerClient;
import com.cognitio.goti.WifiApControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Waiting extends AppCompatActivity {

    WifiApControl wifiApControl;
    WifiManager mWifiManager;
    FileServerAsyncTask receiveTask;
    Send senTask;
    LongReceive task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        mWifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
        wifiApControl= WifiApControl.getApControl(mWifiManager,this);

//        task=new LongReceive(this);
//        task.execute();
    }

    @Override
    public void onBackPressed() {
//        task.cancel(true);
//        Log.e("back","pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Quit Game?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                task.cancel(true);
//                new ServerClient.CloseSocket(Waiting.this,8081).execute();
                wifiApControl.getClientList(Waiting.this, true, 3000, new WifiApControl.FinishScanListener() {
                    @Override
                    public void onFinishScan(ArrayList<String> resultIPAddr) {
                        Log.e("clients", resultIPAddr.size() + "");
                        if (resultIPAddr.size() > 0)
                            for (int i = 0; i < resultIPAddr.size(); i++) {
                                if (senTask != null)
                                    senTask.cancel(true);

                                JSONObject toSend = new JSONObject();
                                try {
                                    toSend.put("method", "remove");
//                                    toSend.put("name", name.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                senTask = new Send(Waiting.this, toSend.toString(), resultIPAddr.get(i));
                                senTask.execute();
                            }
                        else{
                            startActivity(new Intent(Waiting.this,StudentHome.class));
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                task.cancel(true);
//                task = new LongReceive(Waiting.this);
//                task.execute();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public  class FileServerAsyncTask extends AsyncTask {

        private Context context;
        private TextView statusText;
        private String text;
        private String currentClientIP;
        private ServerSocket serverSocket;

        public FileServerAsyncTask(Context context) {
            this.context = context;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(text!=null) {
                Log.e("response", text);
                Toast.makeText(context,text,Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context,StudentHome.class));
            }
//        new ServerClient.Send(context,"You are Added",currentClientIP).execute();
//        task=new CreateHotspot.FileServerAsyncTask(context);
//        task.execute();
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
                serverSocket = new ServerSocket(8888);
                serverSocket.setSoTimeout(2000);
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
//            players.add(new Player(text,client.getInetAddress()+""));
                currentClientIP = client.getInetAddress().toString().substring(1);

//                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
//                return f.getAbsolutePath();
                return null;
            } catch (IOException e) {
                Log.e("error", e.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        // some code #3 (Write your code here to run in UI thread)
                        Toast.makeText(context,"Looks like you were connected to the wrong hotspot!",Toast.LENGTH_LONG).show();
                        context.startActivity(new Intent(context,StudentHome.class));

                    }
                });

                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
        }
    }
    public  class Send extends AsyncTask {
        String hello, host;
        Context context;

        public Send(Context context, String hello, String host) {
            this.hello = hello;
            this.host = host;
            this.context = context;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("sent","sent");

            if(receiveTask!=null)
                receiveTask.cancel(true);
            receiveTask = new FileServerAsyncTask(Waiting.this);
            receiveTask.execute();
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
                socket.connect((new InetSocketAddress(host, port)), 500);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(hello.getBytes(Charset.forName("UTF-8")));
//            ContentResolver cr = context.getContentResolver();
//            InputStream inputStream = null;
//            inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
//            while ((len = inputStream.read(buf)) != -1) {
//                outputStream.write(buf, 0, len);
//            }
                outputStream.close();
//            inputStream.close();
            } catch (FileNotFoundException e) {
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



    public static class LongReceive extends AsyncTask {

        private Context context;
        private TextView statusText;
        private String text;

        public LongReceive(Context context) {
            this.context = context;

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("quiz started",text);
            context.startActivity(new Intent(context,Quiz.class));
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {

                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */

                ServerSocket serverSocket = new ServerSocket(8888);
//                serverSocket.setSoTimeout(1000);
//                while(!isCancelled()) {
//                    try {
                        Socket client = serverSocket.accept();
                        BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            total.append(line).append('\n');
                        }

                        text = total.toString();
                        text += client.getInetAddress();
                        Log.e("qyi","quiz started");
//                        break;
//                    }catch (SocketTimeoutException e){
//                        Log.e("timeout","timeout");
//                    }
//                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));

//                }


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
                serverSocket.close();
//                return f.getAbsolutePath();
                return text;
            } catch (SocketException e){
                Log.e("socket exception","socket closed by another thread"+e.toString());
                return null;
            }catch (IOException e) {
                Log.e("error", e.getMessage());
                return null;
            }
        }
    }



}
