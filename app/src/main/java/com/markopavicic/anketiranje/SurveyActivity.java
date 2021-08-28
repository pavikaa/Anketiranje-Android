package com.markopavicic.anketiranje;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markopavicic.anketiranje.surveyRecycler.AnswersClickListener;
import com.markopavicic.anketiranje.surveyRecycler.AnswersRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SurveyActivity extends AppCompatActivity implements AnswersClickListener {

    private RecyclerView answersRecycler;
    private AnswersRecyclerAdapter answersRecyclerAdapter;
    private TextView tvQuestion, tvTimeLeft;
    private List<List<String>> answersList;
    private List<String> answerList;
    private List<String> questionsList;
    private String id;
    private int solvingTimeMins;
    private Boolean loggedIn;
    private int counter, previousValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Intent i = getIntent();
        id = i.getStringExtra("surveyID");
        solvingTimeMins = i.getIntExtra("solvingTimeMins", 0);
        loggedIn = i.getBooleanExtra("loggedIn", true);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimeLeft = findViewById(R.id.tvTimeLeft);
        setupRecycler();
        loadDataFromFirebase();
        counter = 0;
        new CountDownTimer(solvingTimeMins * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimeLeft.setText("Preostalo vrijeme (s): " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Intent i = new Intent(getBaseContext(), SurveyCompleteSplash.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("loggedIn", loggedIn);
                i.putExtra("surveyID", id);
                startActivity(i);
                finish();
            }
        }.start();
    }

    private void setupRecycler() {
        answersRecycler = findViewById(R.id.answersRecyclerView);
        answersRecycler.setLayoutManager(new LinearLayoutManager(this));
        answersRecyclerAdapter = new AnswersRecyclerAdapter(this);
        answersRecycler.setAdapter(answersRecyclerAdapter);
    }

    private void loadDataFromFirebase() {
        answersList = new ArrayList<>();
        questionsList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("ankete").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot questionSnapshot : snapshot.child("pitanja").getChildren()) {
                            answerList = new ArrayList<>();
                            questionsList.add(questionSnapshot.child("0").getValue().toString());
                            for (DataSnapshot answerSnapshot : questionSnapshot.child("1").getChildren()) {
                                answerList.add(answerSnapshot.child("0").getValue().toString());
                            }
                            answersList.add(answerList);
                        }
                        setupData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setupData() {
        tvQuestion.setText(questionsList.get(counter));
        answersRecyclerAdapter.addData(answersList.get(counter));
    }

    @Override
    public void AnswerClick(int position) {
        FirebaseDatabase.getInstance().getReference("ankete").child(id).child("pitanja").child(Integer.toString(counter)).child("1").child(Integer.toString(position)).child("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                previousValue = snapshot.getValue(Integer.class);
                snapshot.getRef().setValue(previousValue += 1).addOnCompleteListener(task -> {
                    counter++;
                    if (counter < questionsList.size()) {
                        setupData();
                    } else {
                        Intent i = new Intent(getBaseContext(), SurveyCompleteSplash.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("loggedIn", loggedIn);
                        i.putExtra("surveyID", id);
                        startActivity(i);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}