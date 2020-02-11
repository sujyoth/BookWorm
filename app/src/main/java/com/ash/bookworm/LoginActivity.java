package com.ash.bookworm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ash.bookworm.Utilities.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private Button loginBtn;
    private TextView notRegisteredTv;

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
                if(!allFieldsValid())
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

        notRegisteredTv.setOnClickListener(new View.OnClickListener() {
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
        }
    }

    private boolean allFieldsValid() {
        if (Util.isEmpty(emailEt)) {
            emailEt.setError("Email cannot be empty");
            return false;
        }
        if (Util.isEmpty(passwordEt)) {
            passwordEt.setError("Password cannot be empty");
            return false;
        }
        if (!Util.isValidEmail(emailEt.getText().toString())) {
            emailEt.setError("Email must be valid");
            return false;
        }
        if (passwordEt.getText().toString().length() < 6) {
            passwordEt.setError("Password must have at least 6 characters");
        }

        return true;
    }


    private void findViews() {
        emailEt = findViewById(R.id.et_email);
        passwordEt = findViewById(R.id.et_password);

        loginBtn = findViewById(R.id.btn_login);

        notRegisteredTv = findViewById(R.id.tv_not_registered);
    }
}
