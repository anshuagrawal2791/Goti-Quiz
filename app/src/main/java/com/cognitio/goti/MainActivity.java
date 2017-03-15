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
        String dummy = "[{\"quiz_name\":\"dummy_quiz\",\"no_of_questions\":5,\"created_at\":\"2017-03-14T18:30\",\"questions\":[\n" +
                "{\"question\":\"ajhdkfjhdshifddjhfduhvuhiuchvuch\",\n" +
                "\"options\":[\"ha\",\"haha\",\"hahaha\",\"hahahaha\"]\n" +
                "},\n" +
                "{\n" +
                "  \"question\":\"fiuhiuchiuvc\",\n" +
                "\"options\":[\"ha\",\"haha\",\"hahaha\"]\n" +
                "},\n" +
                "{\n" +
                "  \"question\":\"ajdhfuisdiufhdsihfiusdhiufdhdisuhfiudhsdiuhfdisuhiudhviuhciuhvciuhiuahkdjfkasfjdahiuhfdkj\",\n" +
                "\"options\":[\"ha\",\"haha\",\"hahaha\",\"hahahaha\",\"fdjdisuhf\",\"dufidhfiuds\"]\n" +
                "},\n" +
                "{\n" +
                "  \"question\":\"ajhdk\",\n" +
                "\"options\":[\"ha\",\"haha\",\"hahaha\",\"hahahaha\",\"dhfdiudhaiudhfiauhdiuahiufdjshfius\",\"djfsiduhfidsuhfiusd\"]\n" +
                "},\n" +
                "{\n" +
                "  \"question\":\"ajhdkfjhdshifddjhfduhvuhiuchvuch\",\n" +
                "\"options\":[\"true\",\"false\"]\n" +
                "}\n" +
                "]},{\"quiz_name\":\"dummy_quiz2\",\"no_of_questions\":5,\"created_at\":\"2017-03-14T18:30\",\"questions\":[\n" +
                "{\"question\":\"ajhdkfjhdshifddjhfduhvuhiuchvuch\",\n" +
                "\"options\":[\"ha\",\"haha\",\"hahaha\",\"hahahaha\"]\n" +
                "},\n" +
                "{\n" +
                "  \"question\":\"fiuhiuchiuvc\",\n" +
                "\"options\":[\"ha\",\"haha\",\"hahaha\"]\n" +
                "},\n" +
                "{\n" +
                "  \"question\":\"ajdhfuisdiufhdsihfiusdhiufdhdisuhfiudhsdiuhfdisuhiudhviuhciuhvciuhiuahkdjfkasfjdahiuhfdkj\",\n" +
                "\"options\":[\"ha\",\"haha\",\"hahaha\",\"hahahaha\",\"fdjdisuhf\",\"dufidhfiuds\"]\n" +
                "},\n" +
                "{\n" +
                "  \"question\":\"ajhdk\",\n" +
                "\"options\":[\"ha\",\"haha\",\"hahaha\",\"hahahaha\",\"dhfdiudhaiudhfiauhdiuahiufdjshfius\",\"djfsiduhfidsuhfiusd\"]\n" +
                "},\n" +
                "{\n" +
                "  \"question\":\"ajhdkfjhdshifddjhfduhvuhiuchvuch\",\n" +
                "\"options\":[\"true\",\"false\"]\n" +
                "}\n" +
                "]}]";
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
