package com.example.testcakes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private EditText firstName, lastName, email, password, phone;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String firstNameText = firstName.getText().toString();
        final String lastNameText = lastName.getText().toString();
        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();
        final String phoneText = phone.getText().toString();

        if (TextUtils.isEmpty(firstNameText) || TextUtils.isEmpty(lastNameText) ||
                TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(phoneText)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            User newUser = new User(firstNameText, lastNameText, emailText, phoneText);

                            mDatabase.child("Users").child(phoneText).setValue(newUser)
                                    .addOnCompleteListener(Register.this, task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Register.this, Login.class));
                                            finish();
                                        } else {
                                            Toast.makeText(Register.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
