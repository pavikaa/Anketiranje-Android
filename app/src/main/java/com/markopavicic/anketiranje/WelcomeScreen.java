package com.markopavicic.anketiranje;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvContinueWithoutRegistration;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        tvRegister = findViewById(R.id.tvRegister);
        tvContinueWithoutRegistration = findViewById(R.id.tvContinueWithoutRegistration);
        tvContinueWithoutRegistration.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            Intent i = new Intent(WelcomeScreen.this, HomeScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("")) {
                    etEmail.setError(getString(R.string.errorEmptyFields));
                    etPassword.setError(getString(R.string.errorEmptyFields));
                    etEmail.requestFocus();
                    etPassword.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                    etEmail.setError(getString(R.string.errorEmail));
                    etEmail.requestFocus();
                } else if (etPassword.getText().toString().length() < 6) {
                    etPassword.setError(getString(R.string.errorPasswordLength));
                    etPassword.requestFocus();
                } else
                    userLogin();
                break;
            case R.id.tvRegister:
                startActivity(new Intent(WelcomeScreen.this, RegisterActivity.class));
                break;
            case R.id.tvContinueWithoutRegistration:
                Intent i = new Intent(getApplicationContext(), EnterSurveyKeyActivity.class);
                startActivity(i);
        }
    }

    private void userLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.isEmailVerified()) {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(WelcomeScreen.this, HomeScreen.class));
                    finish();
                } else {
                    user.sendEmailVerification();
                    Toast.makeText(WelcomeScreen.this, (R.string.errorEmailVerification), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(WelcomeScreen.this, (R.string.errorLogin), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}