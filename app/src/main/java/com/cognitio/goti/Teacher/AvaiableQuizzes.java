package com.cognitio.goti.Teacher;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cognitio.goti.Adapters.AvailableQuizzesAdapter;
import com.cognitio.goti.Constants;
import com.cognitio.goti.R;

import org.json.JSONArray;
import org.json.JSONException;

public class AvaiableQuizzes extends AppCompatActivity {

    RecyclerView recycler;
    AvailableQuizzesAdapter myadapter;
    JSONArray quizzesJSONArray;
    SharedPreferences quizzesPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaiable_quizzes);
        recycler = (RecyclerView)findViewById(R.id.recycler);
        myadapter = new AvailableQuizzesAdapter(this);
        quizzesPreference = getSharedPreferences(Constants.QUIZZES_PREFERENCE_FILE,MODE_PRIVATE);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recycler.setAdapter(myadapter);
        try {
            quizzesJSONArray= new JSONArray(quizzesPreference.getString(Constants.QUIZZES_PREFERENCE_KEY,Constants.QUIZZES_PREFERENCE_DEFAULT));
            myadapter.set(quizzesJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
