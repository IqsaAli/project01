package com.example.project01;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.button3);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Please enter your email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Please enter your password");
                    return;
                }

                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Login successful, navigate to home activity
                            startActivity(new Intent(LogIn.this, Home.class));
                            finish(); // Close the login activity
                        }
                    } else {
                        // Login failed, show error message
                        startActivity(new Intent(LogIn.this, SignUp.class));
                        finish();
                    }
                });
    }
    public void gotoSignUp(View view){
        Intent I1 = new Intent(LogIn.this,SignUp.class);
        startActivity(I1);
    }

    public void gotoHome(View view){
        Intent I1 = new Intent(LogIn.this,Home.class);
        startActivity(I1);
    }

}

