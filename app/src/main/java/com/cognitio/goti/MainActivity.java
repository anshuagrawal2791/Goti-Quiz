package com.cognitio.goti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cognitio.goti.Student.StudentHome;
import com.cognitio.goti.Teacher.TeacherHome;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    Button teacher,student;
    SharedPreferences quizzes;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        teacher = (Button)findViewById(R.id.teacher);
        student = (Button)findViewById(R.id.student);
        quizzes = getSharedPreferences(Constants.QUIZZES_PREFERENCE_FILE,MODE_PRIVATE);
        editor = quizzes.edit();
        editor.clear();                                                             //for testing
        String dummy = "[{\"quiz_name\":\"dummy_quiz\",\"no_of_questions\":5,\"created_at\":\"2017-03-14T18:30\",\"questions\":[{\"question\":\"ajhdkfjhdshifddjhfduhvuhiuchvuch\",\"options\":[\"ha\",\"haha\",\"hahaha\",\"hahahaha\"]},{\"question\":\"fiuhiuchiuvc\",\"options\":[\"ha\",\"haha\",\"hahaha\"]},{\"question\":\"ajdhfuisdiufhdsihfiusdhiufdhdisuhfiudhsdiuhfdisuhiudhviuhciuhvciuhiuahkdjfkasfjdahiuhfdkj\",\"options\":[\"ha\",\"haha\",\"hahaha\",\"dufidhfiuds\"]},{\"question\":\"ajhdk\",\"options\":[\"ha\",\"dhfdiudhaiudhfiauhdiuahiufdjshfius\",\"djfsiduhfidsuhfiusd\"]},{\"question\":\"ajhdkfjhdshifddjhfduhvuhiuchvuch\",\"options\":[\"true\",\"false\"]}],\"answers\":[\"a\",\"c\",\"d\",\"b\",\"b\"]},{\"quiz_name\":\"dummy_quiz2\",\"no_of_questions\":5,\"created_at\":\"2017-03-14T18:30\",\"questions\":[{\"question\":\"ajhdkfjhdshifddjhfduhvuhiuchvuch\",\"options\":[\"ha\",\"haha\",\"hahaha\",\"hahahaha\"]},{\"question\":\"fiuhiuchiuvc\",\"options\":[\"ha\",\"haha\",\"hahaha\"]},{\"question\":\"ajdhfuisdiufhdsihfiusdhiufdhdisuhfiudhsdiuhfdisuhiudhviuhciuhvciuhiuahkdjfkasfjdahiuhfdkj\",\"options\":[\"ha\",\"haha\",\"dufidhfiuds\"]},{\"question\":\"ajhdk\",\"options\":[\"ha\",\"hahahaha\",\"dhfdiudhaiudhfiauhdiuahiufdjshfius\",\"djfsiduhfidsuhfiusd\"]},{\"question\":\"ajhdkfjhdshifddjhfduhvuhiuchvuch\",\"options\":[\"true\",\"false\"]}],\"answers\":[\"a\",\"c\",\"d\",\"b\",\"b\"]}]";
        editor.putString(Constants.QUIZZES_PREFERENCE_KEY,dummy);
//        editor.putString(Constants.QUIZZES_PREFERENCE_KEY, getResources().getString(R.string.quizzes)); //for testing
        editor.putString(Constants.QUIZZES_PREFERENCE_PASSWORD, getResources().getString(R.string.password)); //for testing
        editor.commit();

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,TeacherHome.class));
            }
        });
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,StudentHome.class));
            }
        });
    }
}
