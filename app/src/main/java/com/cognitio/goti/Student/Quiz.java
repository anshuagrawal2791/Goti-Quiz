package com.cognitio.goti.Student;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cognitio.goti.MainActivity;
import com.cognitio.goti.R;

import org.json.JSONArray;
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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

public class Quiz extends AppCompatActivity {

    TextView timer_tv, question;
    Button b1, b2, b3, b4, submit;
    String dialogMessage;
    AlertDialog quizStartDialog;
    LongReceiver questionReceiver;
    String server;
    int questionNumber=0;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz2);
        Intent intent = getIntent();
        server = intent.getStringExtra("server");
        timer_tv = (TextView)findViewById(R.id.timer_student);
        question = (TextView) findViewById(R.id.question);
        b1 = (Button) findViewById(R.id.optionA);
        b2 = (Button) findViewById(R.id.optionB);
        b3 = (Button) findViewById(R.id.optionC);
        b4 = (Button) findViewById(R.id.optionD);
//        submit = (Button)findViewById(R.id.submit_answer);

        questionReceiver = new LongReceiver(this);
        questionReceiver.execute();
        quizStartDialog = new AlertDialog.Builder(Quiz.this).create();
        quizStartDialog.setTitle("Question Coming Up");
        quizStartDialog.setMessage("Wait for it...");
        quizStartDialog.setCancelable(false);
        quizStartDialog.show();

        b1.setTag("a");
        b2.setTag("b");
        b3.setTag("c");
        b4.setTag("d");
//        submit.
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                submitAndPrepareForNext(view.getTag().toString(),timer_tv.getText().toString());

            }
        };
        b1.setOnClickListener(listener);
        b2.setOnClickListener(listener);
        b3.setOnClickListener(listener);
        b4.setOnClickListener(listener);


    }

    private void submitAndPrepareForNext(String answer,String time) {

        JSONObject toSend = new JSONObject();
        try {
            toSend.put("method", "answer");
            toSend.put("answer",answer);
            toSend.put("time",time);
            toSend.put("question_no",questionNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Send(Quiz.this, toSend.toString(), server).execute();
        quizStartDialog.show();
        question.setText(" ");
        setButtonsInvisible();
        timer_tv.setText("00");
        questionNumber++;
        questionReceiver = new LongReceiver(this);
        questionReceiver.execute();


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Quit quiz?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (questionReceiver != null)
                    questionReceiver.cancel(true);
                JSONObject toSend = new JSONObject();
                try {
                    toSend.put("method", "remove");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Send(Quiz.this, toSend.toString(), server).execute();
                Intent intent = new Intent(Quiz.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

//        super.onBackPressed();
    }

    private void setButtonsVisible(int length) {
        if (length > 0) {
            b1.setVisibility(View.VISIBLE);
            length--;
        }
        if (length > 0) {
            b2.setVisibility(View.VISIBLE);
            length--;
        }
        if (length > 0) {
            b3.setVisibility(View.VISIBLE);
            length--;
        }
        if (length > 0) {
            b4.setVisibility(View.VISIBLE);
            length--;
        }

    }

    private void setButtonsInvisible() {
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        b3.setVisibility(View.GONE);
        b4.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (questionReceiver != null)
            questionReceiver.cancel(true);
        super.onDestroy();
    }


    public class LongReceiver extends AsyncTask {

        private Context context;
        private TextView statusText;
        private String text;

        public LongReceiver(Context context) {
            this.context = context;

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(text!=null) {
                if(!text.equals("over")) {
                    Log.e("question received", text);
                    questionReceiver.cancel(true);
                    try {
                        final JSONObject currentQuestion = new JSONObject(text);
                        Log.e("question", currentQuestion.toString());
                        question.setText(currentQuestion.getString("question"));
                        JSONArray options = currentQuestion.getJSONArray("options");
                        int l = options.length();
                        setButtonsVisible(l);
                        int m = l;
                        if (m > 0) {
                            b1.setText(options.getString(l - m));
                            m--;
                        }
                        if (m > 0) {
                            b2.setText(options.getString(l - m));
                            m--;
                        }
                        if (m > 0) {
                            b3.setText(options.getString(l - m));
                            m--;
                        }
                        if (m > 0) {
                            b4.setText(options.getString(l - m));
                            m--;
                        }
                        final int time = currentQuestion.getInt("time");
                        timer_tv.setText(time + "");
                        timer = new CountDownTimer(time * 1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                timer_tv.setText("" + millisUntilFinished / 1000);
                            }

                            public void onFinish() {
                                submitAndPrepareForNext("na", time + "");
                            }
                        }.start();
                        quizStartDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    questionReceiver.cancel(true);
                    Toast.makeText(context,"Questions over",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,Results.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }else{
//                questionReceiver = new LongReceiver(context);
//                questionReceiver.execute();
            }
//            context.startActivity(new Intent(context,Quiz.class));
        }

        @Override
        protected void onCancelled() {
            Log.e("onCancelled", "called");
            super.onCancelled();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {

                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */

//                ServerSocket serverSocket = new ServerSocket();
//                serverSocket.setReuseAddress(true);
//                serverSocket.bind(new InetSocketAddress(8887));
//                serverSocket.setSoTimeout(1000);
//                while (!isCancelled()) {
//                    Socket client = serverSocket.accept();
////                        BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                    InputStream is = client.getInputStream();
//                    InputStreamReader isr = new InputStreamReader(is);
//                    BufferedReader br = new BufferedReader(isr);
//                    StringBuilder total = new StringBuilder();
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        total.append(line).append('\n');
//                    }
//
//                    text = total.toString();
////                        text += client.getInetAddress();
//                    Log.e("qyi", text);
////                    is.close();
//                    serverSocket.close();
//                    break;
//                }
                ServerSocket serverSocket = new ServerSocket(8085);
                serverSocket.setSoTimeout(1000);
                while(!isCancelled()) {
                    try {
                        Socket client = serverSocket.accept();
//                        BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                StringBuilder total = new StringBuilder();
//                String line;
//                while ((line = r.readLine()) != null) {
//                    Log.e("total",total.toString());
//                    total.append(line).append('\n');
//                }
                        text = r.readLine();
                        Log.e("qyi",text);

                        OutputStream os = client.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os);
                        BufferedWriter bw = new BufferedWriter(osw);
                        bw.write("received");
                        bw.newLine();
                        bw.flush();

                        os.close();
//                        is.close();
                        break;
                    }catch (SocketTimeoutException e){
                        Log.e("timeout","timeout");
                    }
//                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));

                }


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
            } catch (SocketException e) {
                Log.e("socket exception", "socket closed by another thread" + e.toString());
                return null;
            } catch (IOException e) {
//                Log.e("error", e.getMessage());
                return null;
            }
        }
    }


    public class Send extends AsyncTask {
        String hello, host;
        Context context;
        //        Boolean approved=false;
        String response;
        int maxRetry=5;

        public Send(Context context, String hello, String host) {
            this.hello = hello;
            this.host = host;
            this.context = context;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("sent", "sent");
            if(!response.equals("recorded")&&maxRetry>0) {
                new Send(context, hello, host).execute();
                maxRetry--;
            }

//            if(approved){
//                Toast.makeText(context,"You're added!",Toast.LENGTH_LONG).show();
//                context.startActivity(new Intent(context,Waiting.class));}
//            else{
//                Toast.makeText(context,"you don't seem to be connected to the right hotspot",Toast.LENGTH_LONG).show();
//            }

//            if(receiveTask!=null)
//                receiveTask.cancel(true);
//            receiveTask = new FileServerAsyncTask(StudentHome.this);
//            receiveTask.execute();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            int port = 8083;
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
                System.out.println("Message received from the server : " + response);

//                if(response!=null)
//                    approved=true;
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
            } catch (SocketTimeoutException e) {
                try {
                    socket.close();
                    Log.e("exception", e.toString());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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

}
