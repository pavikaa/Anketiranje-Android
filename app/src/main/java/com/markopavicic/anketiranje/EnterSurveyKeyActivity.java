package com.markopavicic.anketiranje;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EnterSurveyKeyActivity extends AppCompatActivity {
    private EditText etSurveyCode;
    private Button btnStartSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_survey_key);
        etSurveyCode = findViewById(R.id.etSurveyCode);
        btnStartSurvey = findViewById(R.id.btnStartSurveyWithoutRegistration);
        btnStartSurvey.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SurveyPrepActivity.class);
            String id = etSurveyCode.getText().toString().trim();
            if (!id.equals("")) {
                i.putExtra("surveyKey", id);
                i.putExtra("loggedIn", false);
                startActivity(i);
            } else {
                etSurveyCode.setError(getString(R.string.errorEmptyFields));
                etSurveyCode.requestFocus();
            }
        });
    }
}