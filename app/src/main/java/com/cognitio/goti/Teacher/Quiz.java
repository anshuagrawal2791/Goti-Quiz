package com.cognitio.goti.Teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cognitio.goti.Constants;
import com.cognitio.goti.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Quiz extends AppCompatActivity {
    String quizToPlay;
    HashMap<String,String> players;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent intent = getIntent();
        quizToPlay=intent.getStringExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY);
        players= (HashMap<String,String>)(intent.getSerializableExtra(Constants.INTENT_FINAL_LIST));
        Log.e("final_list",players.toString());
        Log.e("quiztoplay",quizToPlay);
//        Log.e(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY,intent.getStringExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY));
//        Log.e(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY,intent.getSerializableExtra("clients").toString());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Quiz.this,CreateHotspot.class);
        intent.putExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY,quizToPlay);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        startActivity(new Intent(Quiz.this,AvaiableQuizzes.class));
    }
}
