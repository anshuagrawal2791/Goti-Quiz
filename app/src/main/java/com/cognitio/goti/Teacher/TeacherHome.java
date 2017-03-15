package com.cognitio.goti.Teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cognitio.goti.Constants;
import com.cognitio.goti.R;
import com.cognitio.goti.ToastMessages;

public class TeacherHome extends AppCompatActivity {
    SharedPreferences quizzes;
    Button create,host;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        quizzes = getSharedPreferences(Constants.QUIZZES_PREFERENCE_FILE, Context.MODE_PRIVATE);
        create = (Button)findViewById(R.id.create_quiz);
        host = (Button)findViewById(R.id.host_quiz);

        Log.e("available quizzes",quizzes.getString(Constants.QUIZZES_PREFERENCE_KEY,Constants.QUIZZES_PREFERENCE_DEFAULT));

        if(quizzes.getString(Constants.QUIZZES_PREFERENCE_KEY,Constants.QUIZZES_PREFERENCE_DEFAULT).equals(Constants.QUIZZES_PREFERENCE_DEFAULT)){
            host.setEnabled(false);
            Toast.makeText(this, ToastMessages.NO_QUIZ_AVAILABLE,Toast.LENGTH_LONG).show();
        }
        else {
            host.setEnabled(true);
        }
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this,AvaiableQuizzes.class));
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this,CreateQuiz.class));
            }
        });



    }
}
