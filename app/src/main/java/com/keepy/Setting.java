package com.keepy;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.keepy.models.User;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.keepy.viewmodel.AppViewModel;

import com.keepy.models.ServiceRequest;

public class Setting extends AppCompatActivity {

    private AppViewModel viewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        findViewById(R.id.changePsBtn).setOnClickListener(view ->
                opensignuppage());

        findViewById(R.id.aboutUsBtn).setOnClickListener(view ->
                openabotus());

        findViewById(R.id.deleteAcBtn).setOnClickListener(view ->
                onDelete());
    }
    public void onDelete() {
        AlertDialog.Builder v = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your user?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            viewModel.deleteUser(this);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
    public void opensignuppage() {
        Intent Intent = new Intent(Setting.this, ResetPassword.class);
        startActivity(Intent);
    }
    public void openMainPage() {
        Intent Intent = new Intent(Setting.this,MainActivity.class);
        startActivity(Intent);
    }
    public void openabotus() {
        Intent Intent = new Intent(Setting.this,AboutUs.class);
        startActivity(Intent);
    }



}