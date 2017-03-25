package com.cognitio.goti;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.cognitio.goti.Student.Waiting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by anshu on 22/03/17.
 */

public class ServerClient {

    public static class CloseSocket extends AsyncTask{
        int port;
        Context context;
        public CloseSocket(Context context,int port) {
            this.port=port;
            this.context=context;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public static class Send extends AsyncTask {
        String hello, host;
        Context context;

        public Send(Context context, String hello, String host,int port) {
            this.hello = hello;
            this.host = host;
            this.context = context;
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


    public static class Receive extends AsyncTask {

        private Context context;
        private TextView statusText;
        private String text;

        public Receive(Context context) {
            this.context = context;

        }



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
                text+=client.getInetAddress();
//                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
//                return f.getAbsolutePath();
                return text;
            } catch (IOException e) {
                Log.e("error", e.getMessage());
                return null;
            }
        }
    }
}