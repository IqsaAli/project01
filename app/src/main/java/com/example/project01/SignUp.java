package com.example.project01;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextTextEmailAddress2);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
        confirmPasswordEditText = findViewById(R.id.editTextTextPassword3);
        registerButton = findViewById(R.id.button5);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Please enter your email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Please enter your password");
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    confirmPasswordEditText.setError("Please confirm your password");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Passwords do not match");
                    return;
                }

                registerUser(email, password);
            }
        });
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful, navigate to login activity
                        Toast.makeText(SignUp.this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, LogIn.class));
                        finish(); // Close the signup activity
                    } else {
                        // Registration failed, show error message
                        Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}
