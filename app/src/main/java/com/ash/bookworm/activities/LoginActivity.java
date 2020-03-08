package com.ash.bookworm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ash.bookworm.R;
import com.ash.bookworm.helpers.utilities.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private TextInputLayout emailTil, passwordTil;
    private Button loginBtn;
    private Button notRegisteredBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        findViews();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allFieldsValid())
                    return;

                final String email = emailEt.getText().toString();
                final String password = passwordEt.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Authentication successful.",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Wrong username/password combination.",
                                            Toast.LENGTH_SHORT).show();

                                    passwordEt.setText("");
                                }
                            }
                        });


            }
        });

        notRegisteredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Comment out the below line for testing login and register activities
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    private boolean allFieldsValid() {
        if (Util.isEmpty(emailEt)) {
            emailTil.setError("Email cannot be empty");
            return false;
        } else {
            emailTil.setError(null);
        }
        if (Util.isEmpty(passwordEt)) {
            passwordTil.setError("Password cannot be empty");
            return false;
        } else {
            passwordTil.setError(null);
        }
        if (!Util.isValidEmail(emailEt.getText().toString())) {
            emailTil.setError("Email must be valid");
            return false;
        } else {
            emailTil.setError(null);
        }
        if (passwordEt.getText().toString().length() < 6) {
            passwordTil.setError("Password must have at least 6 characters");
            return false;
        } else {
            passwordTil.setError(null);
        }

        return true;
    }


    private void findViews() {
        emailEt = findViewById(R.id.et_email);
        passwordEt = findViewById(R.id.et_password);

        emailTil = findViewById(R.id.til_email);
        passwordTil = findViewById(R.id.til_password);

        loginBtn = findViewById(R.id.btn_login);

        notRegisteredBtn = findViewById(R.id.btn_not_registered);
    }
}
