package com.keepy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User != null)
            openHomepage();

        Button button = findViewById(R.id.Buttonlogin);
        button.setOnClickListener(view -> openloginpage());

        findViewById(R.id.ButtonSignUp).setOnClickListener(view ->
                opensignuppage());



    }

    public static TextWatcher getProfileTextWatcher(
            Activity context,
            Predicate<String> xyz) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (xyz.test(editable.toString())) {
                        if (context instanceof EditProfileClient) {
                            context.findViewById(R.id.saveChanges_Client).setEnabled(false);
                        } else if (context instanceof EditProfileKeepy) {
                            context.findViewById(R.id.saveChanges_Keeper).setEnabled(false);
                        }
                    } else {
                        if (context instanceof EditProfileClient) {
                            context.findViewById(R.id.saveChanges_Client).setEnabled(true);
                        } else if (context instanceof EditProfileKeepy) {
                            context.findViewById(R.id.saveChanges_Keeper).setEnabled(true);
                        }
                    }
                }
            }
        };
    }

    public void openHomepage() {
        Intent Intent = new Intent(MainActivity.this, UserPage.class);
        startActivity(Intent);
    }

    public void openloginpage() {
        Intent Intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(Intent);
    }

    public void opensignuppage() {
        Intent Intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(Intent);
    }


}