package com.cognitio.goti.Adapters;

import android.content.Context;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cognitio.goti.Constants;
import com.cognitio.goti.R;
import com.cognitio.goti.Teacher.CreateHotspot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by anshu on 14/03/17.
 */

public class AvailableQuizzesAdapter extends RecyclerView.Adapter<AvailableQuizzesAdapter.myviewholder> {
    Context context;
    LayoutInflater inflater;
    JSONArray quizzes = new JSONArray();
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    DateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public AvailableQuizzesAdapter(Context context) {
        this.context =context;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = inflater.inflate(R.layout.available_quizzes_row,parent,false);
        myviewholder viewholder = new myviewholder(v);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(AvailableQuizzesAdapter.myviewholder holder, int position) {
        try {
            final JSONObject current = quizzes.getJSONObject(position);
            holder.name.setText(current.getString(Constants.QUIZZES_QUIZ_NAME));
            holder.noOfQuestions.setText(current.getInt(Constants.QUIZZES_NO_OF_QUESTIONS)+"");
            holder.createdAt.setText(dateFormat.format(parser.parse(current.getString(Constants.QUIZZES_CREATED_AT))));
            holder.icon.setImageResource(R.mipmap.ic_launcher);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CreateHotspot.class);
                    intent.putExtra(Constants.INTENT_EXTRA_KEY_QUIZ_TO_PLAY,current.toString());
                    context.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }



    @Override
    public int getItemCount() {
        return quizzes.length();
    }

    public void set(JSONArray quizzesJSONArray) throws JSONException {
        quizzes=quizzesJSONArray;
//        Log.e("quizzes",quizzesJSONArray.get(0).toString());
//        Log.e("quizzes",quizzesJSONArray.length()+"");

    }

    static class myviewholder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView name,createdAt,noOfQuestions;
        ImageView icon;
        public myviewholder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card);
            name = (TextView)itemView.findViewById(R.id.name);
            createdAt = (TextView)itemView.findViewById(R.id.created_at);
            noOfQuestions = (TextView)itemView.findViewById(R.id.no_of_questions);
            icon = (ImageView)itemView.findViewById(R.id.icon);
        }
    }
}
