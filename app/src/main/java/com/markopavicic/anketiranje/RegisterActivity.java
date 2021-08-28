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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etPassword2;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        tvLogin = findViewById(R.id.tvLogin);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etPassword2 = findViewById(R.id.etPassword2Register);
        register = findViewById(R.id.btnRegister);
        tvLogin.setOnClickListener(this);
        register.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBarRegister);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLogin:
                startActivity(new Intent(this, WelcomeScreen.class));
                finish();
                break;

            case R.id.btnRegister:
                if (etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("") || etPassword2.getText().toString().equals("")) {
                    etEmail.setError(getString(R.string.errorEmptyFields));
                    etPassword.setError(getString(R.string.errorEmptyFields));
                    etPassword2.setError(getString(R.string.errorEmptyFields));
                    etEmail.requestFocus();
                    etPassword.requestFocus();
                    etPassword2.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                    etEmail.setError(getString(R.string.errorEmail));
                    etEmail.requestFocus();
                } else if (!etPassword.getText().toString().equals(etPassword2.getText().toString())) {
                    etPassword.setError(getString(R.string.errorPasswordMissmatch));
                    etPassword2.setError(getString(R.string.errorPasswordMissmatch));
                    etPassword.requestFocus();
                    etPassword2.requestFocus();
                } else if (etPassword.getText().toString().length() < 6) {
                    etPassword.setError(getString(R.string.errorPasswordLength));
                    etPassword.requestFocus();
                } else
                    registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, (R.string.successfulRegistration), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        etEmail.getText().clear();
                        etPassword.getText().clear();
                        etPassword2.getText().clear();
                        startActivity(new Intent(RegisterActivity.this, WelcomeScreen.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, (R.string.unsuccessfulRegistration), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        etPassword.getText().clear();
                        etPassword2.getText().clear();
                    }
                });
    }
}