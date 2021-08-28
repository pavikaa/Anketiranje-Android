package com.markopavicic.anketiranje;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SurveyPrepActivity extends AppCompatActivity {

    private TextView tvSurveyName, tvSurveySolvingTime;
    private Button btnStartSurvey;
    private Boolean loggedIn, check, checkIfAlreadySolved;
    private String code, id;
    private int solvingTimeMins;
    private ArrayList<String> solvedSurveysArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_prep);
        check = false;
        loadData();
        tvSurveyName = findViewById(R.id.tvSurveyNamePrep);
        tvSurveySolvingTime = findViewById(R.id.tvSolvingTime);
        btnStartSurvey = findViewById(R.id.btnStartSurvey);
        btnStartSurvey.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SurveyActivity.class);
            i.putExtra("surveyID", id);
            i.putExtra("loggedIn", loggedIn);
            i.putExtra("solvingTimeMins", solvingTimeMins);
            startActivity(i);
        });
        Intent i = getIntent();
        code = i.getStringExtra("surveyKey");
        loggedIn = i.getBooleanExtra("loggedIn", false);
        setupData();

    }

    private void setupData() {
        checkIfAlreadySolved = false;
        FirebaseDatabase.getInstance().getReference("ankete")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot surveysSnapshot : snapshot.getChildren()) {
                            if (surveysSnapshot.child("kod").getValue().equals(code)) {
                                tvSurveyName.setText("Anketa: " + surveysSnapshot.child("kod").getValue().toString());
                                tvSurveySolvingTime.setText("Vrijeme rješavanja ove ankete je: \n" + surveysSnapshot.child("trajanje").getValue().toString() + " min");
                                solvingTimeMins = Integer.parseInt(surveysSnapshot.child("trajanje").getValue().toString());
                                id = surveysSnapshot.getKey();
                                check = true;
                                for (int i = 0; i < solvedSurveysArrayList.size(); i++) {
                                    if (id.equals(solvedSurveysArrayList.get(i))) {
                                        checkIfAlreadySolved = true;
                                    }
                                }
                            }
                        }
                        if (!check) {
                            tvSurveyName.setVisibility(View.GONE);
                            tvSurveySolvingTime.setVisibility(View.GONE);
                            btnStartSurvey.setVisibility(View.GONE);
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Nepostojeća anketa.", Snackbar.LENGTH_LONG).show();
                        }
                        if (checkIfAlreadySolved) {
                            tvSurveyName.setVisibility(View.GONE);
                            tvSurveySolvingTime.setVisibility(View.GONE);
                            btnStartSurvey.setVisibility(View.GONE);
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Ova anketa je već riješena.", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("surveys", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        solvedSurveysArrayList = gson.fromJson(json, type);
        if (solvedSurveysArrayList == null) {
            solvedSurveysArrayList = new ArrayList<>();
            solvedSurveysArrayList.add("");
        }
    }
}