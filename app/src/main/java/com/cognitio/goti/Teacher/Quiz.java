package com.cognitio.goti.Teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cognitio.goti.Constants;
import com.cognitio.goti.R;
import com.cognitio.goti.ServerClient;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Quiz extends AppCompatActivity {
    String quizToPlay;
    HashMap<String,String> players;
    TextView question;
    EditText timer;
    Button b1,b2,b3,b4,push;
    JSONObject quiz;
    int numOfQuestions;
    JSONArray questions;
    int current=0;
    JSONObject currentQuestion;
    AlertDialog waitingResponse;
    FileServerAsyncTask receivingTask;
    Send sendingTask;
    int numOfResponses=0;

    static class Response{
        int questionNumber;
        String response;
        double time;

        public Response(int questionNumber, String response, double time) {
            this.questionNumber = questionNumber;
            this.response = response;
            this.time = time;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "questionNumber=" + questionNumber +
                    ", response='" + response + '\'' +
                    ", time=" + time +
                    '}';
        }
    }
    static HashMap<String,ArrayList<Response>> responses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent intent = getIntent();
        quizToPlay=intent.getStringExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY);
        players= (HashMap<String,String>)(intent.getSerializableExtra(Constants.INTENT_FINAL_LIST));
        Log.e("final_list",players.toString());
        Log.e("quiztoplay",quizToPlay);

        question = (TextView)findViewById(R.id.questiont);
         timer = (EditText)findViewById(R.id.timer);
        b1= (Button)findViewById(R.id.optionAt);
        b2= (Button)findViewById(R.id.optionBt);
        b3= (Button)findViewById(R.id.optionCt);
        b4= (Button)findViewById(R.id.optionDt);
        push= (Button)findViewById(R.id.push_question);
        responses = new HashMap<>();
        waitingResponse = new AlertDialog.Builder(this).create();
        waitingResponse.setMessage("Waiting for Responses...");
        waitingResponse.setCancelable(false);
        try {
            quiz = new JSONObject(quizToPlay);
            numOfQuestions = quiz.getInt(Constants.QUIZZES_NO_OF_QUESTIONS);
            questions = quiz.getJSONArray(Constants.QUIZZES_QUESTIONS);
            updateCurrentQuestion();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        receivingTask = new FileServerAsyncTask(Quiz.this);
        receivingTask.execute();

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!timer.getText().toString().matches("")) {
                    if(Integer.parseInt(timer.getText().toString())<60) {
                        try {
                            currentQuestion.put("time", Integer.parseInt(timer.getText().toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (current <= numOfQuestions) {
//                    waitingResponse.show();
                            final ProgressDialog dialog = new ProgressDialog(Quiz.this);
                            dialog.show();
                            for (Map.Entry<String, String> entry : players.entrySet()) {
                                new Send(Quiz.this, currentQuestion.toString(), entry.getKey(), 8085, 100).execute();
                            }
                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(Quiz.this, "Question Pushed", Toast.LENGTH_SHORT).show();
                                    waitingResponse.show();

                                }
                            }, 2000);
                        }
                        else{
                            Toast.makeText(Quiz.this,"Questions Over",Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        timer.setError("Time can't be greater than 60");
                    }
                }
                else{
                    timer.setError("Enter time");
                }
            }
        });




//        Log.e(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY,intent.getStringExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY));
//        Log.e(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY,intent.getSerializableExtra("clients").toString());



    }

    private void updateCurrentQuestion() {
        try {
            setButtonsInvisible();
            currentQuestion = questions.getJSONObject(current);
            question.setText(currentQuestion.getString("question"));
            timer.setText("");
            JSONArray options = currentQuestion.getJSONArray("options");
            int l = options.length();
            setButtonsVisible(l);
            int m=l;
            if(m>0){
                b1.setText(options.getString(l-m));
                m--;
            }if(m>0){
                b2.setText(options.getString(l-m));
                m--;
            }
            if(m>0){
                b3.setText(options.getString(l-m));
                m--;
            }
            if(m>0){
                b4.setText(options.getString(l-m));
                m--;
            }
//            currentQuestion.put("time",Integer.parseInt(timer.getText().toString()));
            current++;
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setButtonsVisible(int length) {
        if(length>0)
        {
            b1.setVisibility(View.VISIBLE);
            length--;
        }
        if(length>0){
            b2.setVisibility(View.VISIBLE);
            length--;
        }if(length>0){
            b3.setVisibility(View.VISIBLE);
            length--;
        }if(length>0){
            b4.setVisibility(View.VISIBLE);
            length--;
        }

    }
    private void setButtonsInvisible(){
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        b3.setVisibility(View.GONE);
        b4.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Quiz.this,CreateHotspot.class);
        intent.putExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY,quizToPlay);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        startActivity(new Intent(Quiz.this,AvaiableQuizzes.class));
    }






    public static class Send extends AsyncTask {
        String hello, host;
        Context context;
        int port,timeout;
        boolean received=false;
        int maxRetry=5;
        String response;

        public Send(Context context, String hello, String host,int port,int timeout) {
            this.hello = hello;
            this.host = host;
            this.context = context;
            this.port = port;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.e("sent to",host);
            if(received){
                Log.e("sent","client joined");}
            else{
                Log.e("sent","didn't joing");
                maxRetry--;
                if(maxRetry>0)
                    new Send(context,hello,host,port,timeout).execute();
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            int len;
            Socket socket = new Socket();
            byte buf[] = new byte[1024];
            try {
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), timeout);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
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
                if(response.equals("received"))
                    received=true;
                else
                    received=false;
//                if(response!=null)
//                    approved=true;
//            ContentResolver cr = context.getContentResolver();
//            InputStream inputStream = null;
//            inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
//            while ((len = inputStream.read(buf)) != -1) {
//                outputStream.write(buf, 0, len);
//            }
//                os.close();
//            inputStream.close();
                is.close();
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





    public  class FileServerAsyncTask extends AsyncTask {

        private Context context;
        private TextView statusText;
        private String text;
        private String currentClientIP;
        private String responseMessage=" ";
        private String method=" ";
        private ServerSocket serverSocket;
//        String operation="";

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

//            Log.e("player",players.toString());
//            connectedClientsNumber.setText(players.keySet().size()+"");
//            if(method.equals("answer"))
//            new ServerClient.Send(context,responseMessage,currentClientIP,8888).execute();
//            connectedClientsNumber.setText(players.keySet().size()+"");
//            if()
//            if(currentQuestionnumOfQuestions)
            if(players.keySet().size()==0){
                Toast.makeText(context,"No Players Left",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,CreateHotspot.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(!isCancelled()){
                Log.e("new receiver","created");
                receivingTask=new FileServerAsyncTask(context);
                receivingTask.execute();
            }
            if(method.equals("answer")){
                if(numOfResponses>=players.keySet().size()) {
                    Log.e("responsed",responses.toString());
                    waitingResponse.dismiss();
                    if(current<numOfQuestions)
                    updateCurrentQuestion();
                    else{
                        final ProgressDialog dialog = new ProgressDialog(Quiz.this);
                        dialog.show();
                        for (Map.Entry<String, String> entry : players.entrySet()) {
                            new Send(Quiz.this, "over", entry.getKey(), 8085, 100).execute();
                        }
                        Handler handler = new Handler();
                        receivingTask.cancel(true);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(Quiz.this, "Question Over", Toast.LENGTH_SHORT).show();
//                                waitingResponse.show();
                                Intent intent = new Intent(context,Results.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);

                            }
                        }, 2000);


//                        context.startActivity(new Intent(context,Results.class));
                    }
                }
//                numOfResponses++;
            }


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
                serverSocket = new ServerSocket(8083);
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
//                StringBuilder total = new StringBuilder();
//                String line;
//                while ((line = r.readLine()) != null) {
//                    Log.e("total",total.toString());
//                    total.append(line).append('\n');
//                }
                text = r.readLine();
                Log.e("text",text);
                JSONObject received = new JSONObject(text);
//                method = received.getString("method");
//                Log.e("received",received.toString());
//                if(received.get("method").equals("add"))
//                {
//                    String name = received.getString("name");
//                    String ip = client.getInetAddress().toString().substring(1);
//                    players.put(ip,name);
//                    responseMessage="Successfully added!";
//                    OutputStream os = client.getOutputStream();
//                    OutputStreamWriter osw = new OutputStreamWriter(os);
//                    BufferedWriter bw = new BufferedWriter(osw);
//                    bw.write(responseMessage);
//                    System.out.println("Message sent to the client is "+responseMessage);
//                    bw.flush();
//                    os.close();
//                }

                method = received.getString("method");
                if(method.equals("remove")){
                    String ip = client.getInetAddress().toString().substring(1);
                    players.remove(ip);
                    responseMessage = "Successfully removed!";
                    OutputStream os = client.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write(responseMessage);
                    System.out.println("Message sent to the client is "+responseMessage);
                    bw.flush();
                    os.close();
                }
                else if(method.equals("answer"))
                {
//                    String name = received.getString("name");
                    String ip = client.getInetAddress().toString().substring(1);
//                    players.put(ip,name);
                    numOfResponses++;
                    responseMessage="recorded";
                    OutputStream os = client.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write(responseMessage);
                    System.out.println("Message sent to the client is "+responseMessage);
                    if(!responses.keySet().contains(ip)) {
                        ArrayList<Response> a = new ArrayList<>();
                        a.add(new Response(received.getInt("question_no"), received.getString("answer"), Double.parseDouble(received.getString("time"))));
                        responses.put(ip,a);
                    }else{
                        Response n = new Response(received.getInt("question_no"), received.getString("answer"), Double.parseDouble(received.getString("time")));
                        ArrayList<Response> a = responses.get(ip);
                        a.add(n);
                        responses.put(ip,a);
                    }
                    bw.flush();
                    os.close();
                }
//

//                    players.add(new Player(received.getString("name"),client.getInetAddress().toString().substring(1)));
                currentClientIP = client.getInetAddress().toString().substring(1);

//                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
//                return f.getAbsolutePath();
                return null;
            } catch (IOException e) {
                Log.e("error", e.getMessage());
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    Log.e("error2", e.getMessage());
                }
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    @Override
    protected void onDestroy() {
        receivingTask.cancel(true);
        super.onDestroy();
    }
}
