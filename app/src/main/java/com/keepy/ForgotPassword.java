package com.keepy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText email = findViewById(R.id.email_reset);

        Button reset = findViewById(R.id.Buttonreset);
        TextView back = findViewById(R.id.backLogin);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString();
                if (emailString.isEmpty()) {
                    email.setError("Please enter your email");
                    email.requestFocus();
                } else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailString)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Snackbar.make(v, "Reset password email sent", Snackbar.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(e -> {
                                email.setError("Please enter a valid email");
                            });
                }
            }
        });
    }

    public void openloginpage() {
        Intent Intent = new Intent(ForgotPassword.this, LogIn.class);
        startActivity(Intent);
    }
    public void openresetpage() {
        Intent Intent = new Intent(ForgotPassword.this, ReceiveCode.class);
        startActivity(Intent);
    }


}