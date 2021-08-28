package com.markopavicic.anketiranje;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SurveyCompleteSplash extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 1500;
    private Boolean loggedIn;
    private ArrayList<String> solvedSurveysArrayList;
    private String surveyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_survey_complete_splash);
        loggedIn = i.getBooleanExtra("loggedIn", true);
        surveyID = i.getStringExtra("surveyID");
        loadData();
        new Handler().postDelayed(() -> {
            Intent i1 = new Intent(getBaseContext(), WelcomeScreen.class);
            if (loggedIn)
                i1 = new Intent(getBaseContext(), HomeScreen.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i1);
            finish();
            solvedSurveysArrayList.add(surveyID);
            saveData();
        }, SPLASH_TIME_OUT);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(solvedSurveysArrayList);
        editor.putString("surveys", json);
        editor.apply();
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
        }
    }
}