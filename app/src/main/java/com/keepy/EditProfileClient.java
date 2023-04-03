package com.keepy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.keepy.dialogs.CalendarActivity;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;
import com.keepy.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EditProfileClient extends AppCompatActivity {
    private CheckBox mIsDogisterBtn;
    private CheckBox mIsHouseKeeperBtn;
    private CheckBox mIsTherapistBtn;
    private CheckBox mIsBabysitterBtn;
    private CheckBox mIsBabysitsdisabilitiesBtn;

    ArrayAdapter<String> adapterItems;


    private boolean mIsDogister = false;
    private boolean mIsHouseKeeper = false;
    private boolean mIsTherapist = false;
    private boolean mIsBabysitter = false;
    private boolean mIsBabysitsdisabilities = false;

    private AppViewModel viewModel;

    Spinner location_Edit;
    TextInputEditText nameTv, phoneTv;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_client);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        location_Edit = findViewById(R.id.locationEdit_Client);
        nameTv = findViewById(R.id.FullNameEdit_Client);
        phoneTv = findViewById(R.id.PhoneEdit_Client);

        findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
        });
        findViewById(R.id.profileBtn).setOnClickListener(v -> {
        });
        findViewById(R.id.homeBtn).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.scheduleBtn).setOnClickListener(v -> {
            openSchedule();
        });


        mIsDogisterBtn = findViewById(R.id.Dogister_ClientBtn);
        mIsHouseKeeperBtn = findViewById(R.id.HouseKeeper_Client_Btn);
        mIsTherapistBtn = findViewById(R.id.Therapist_Client_Btn);
        mIsBabysitterBtn = findViewById(R.id.Babysitter_ClientBtn);
        mIsBabysitsdisabilitiesBtn = findViewById(R.id.Babysitsdisabilities_Client_Btn);


        ProgressDialog dialog = new ProgressDialog(this);

        Button saveChangesBtn = findViewById(R.id.saveChanges_Client);
        saveChangesBtn.setEnabled(false);
        saveChangesBtn.setOnClickListener(v -> {
            saveChanges();
        });
        viewModel.loadingLivaData.observe(this, isLoading -> {
            if (isLoading != null) {
                dialog.setTitle(Constants.AppName);
                dialog.setMessage(isLoading);
                dialog.show();
                return;
            }
            dialog.dismiss();
        });
        viewModel.userLiveData.observe(this, user -> {
            nameTv.setText(user.getmFullName());
            phoneTv.setText(user.getmPhone());
            for (int i = 0; i < Constants.places.length; i++) {
                if (Constants.places[i].equals(user.getmLocation())) {
                    location_Edit.setSelection(i, true);
                    break;
                }
            }

            handleClientPrefs(user);
        });

        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, Constants.places);
        location_Edit.setAdapter(adapterItems);


        mIsDogisterBtn.setOnClickListener(v -> {
            saveChangesBtn.setEnabled(true);
            mIsDogister = !mIsDogister;
        });

        mIsHouseKeeperBtn.setOnClickListener(v -> {
            saveChangesBtn.setEnabled(true);
            mIsHouseKeeper = !mIsHouseKeeper;
        });

        mIsTherapistBtn.setOnClickListener(v -> {
            mIsTherapist = !mIsTherapist;
           saveChangesBtn.setEnabled(true);

        });
        mIsBabysitterBtn.setOnClickListener(v -> {
            mIsBabysitter = !mIsBabysitter;
            saveChangesBtn.setEnabled(true);
        });

        mIsBabysitsdisabilitiesBtn.setOnClickListener(v -> {
            mIsBabysitsdisabilities = !mIsBabysitsdisabilities;
            saveChangesBtn.setEnabled(true);
        });

        nameTv.addTextChangedListener(MainActivity.getProfileTextWatcher(this, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return viewModel.userLiveData.getValue()!= null &&
                        s.equals(viewModel.userLiveData.getValue().getmFullName());
            }
        }));
        phoneTv.addTextChangedListener(MainActivity.getProfileTextWatcher(this, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return viewModel.userLiveData.getValue()!= null &&
                        s.equals(viewModel.userLiveData.getValue().getmPhone());
            }
        }));
        location_Edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(viewModel.userLiveData.getValue()!=null &&location_Edit.getSelectedItem().toString().equals(viewModel.userLiveData.getValue().getmLocation()))
                    return;
                findViewById(R.id.saveChanges_Client).setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    public void openSchedule() {
        User user = viewModel.userLiveData.getValue();
        List<ServiceRequest> incomingRequests = viewModel.incomingRequests.getValue();
        List<ServiceRequest> outgoingRequests = viewModel.outgoingRequests.getValue();
        if (user == null || (incomingRequests == null && outgoingRequests == null)) {
            Snackbar.make(findViewById(R.id.mainLayout), "You need to be logged in to use these features", Snackbar.LENGTH_LONG).show();
            return;
        }
        CalendarActivity calendarDialog = new CalendarActivity(
                this,
                viewModel,
                true,
                outgoingRequests,
                null);
        calendarDialog.show();

    }



    private void showToastPhoneError() {
        Snackbar.make(findViewById(R.id.parentLayoutEditClient), "Phone number must be israeli number, 10 digits long!", Snackbar.LENGTH_LONG).show();
    }

    /*
     * This method is used to validate the phone number
     * the phone number must be israeli number, 10 digits long
     */
    private boolean isValidPhone(String phone) {
        if (!Constants
                .Utils
                .isIsraeliPhoneNumber(phone)
        ) {
            showToastPhoneError();
            return false;
        }
        return true;
    }

    private void saveChanges() {
        User old = viewModel.userLiveData.getValue();
        if (old == null) {
            Toast.makeText(this, "Some unknown error has occured, please try again later", Toast.LENGTH_LONG).show();
            return;
        }
        if (nameTv.getText() != null) {
            String name = nameTv.getText().toString();
            old.setmFullName(name);
        }

        boolean hasPhoneTv = phoneTv.getText() != null && !TextUtils.isEmpty(phoneTv.getText().toString());
        if (hasPhoneTv) {
            if (!isValidPhone(phoneTv.getText().toString()))
                return;
            String phone = phoneTv.getText().toString();
            old.setmPhone(phone);
        }
        String location = Constants.places[location_Edit.getSelectedItemPosition()];
        old.setmLocation(location);
        List<String> clientPrefs = new ArrayList<>();

        if (mIsDogister)
            clientPrefs.add(Constants.DogisterType);
        if (mIsHouseKeeper)
            clientPrefs.add(Constants.HouseKeeperType);
        if (mIsTherapist)
            clientPrefs.add(Constants.TherapistType);
        if (mIsBabysitter)
            clientPrefs.add(Constants.BabySitterType);
        if (mIsBabysitsdisabilities)
            clientPrefs.add(Constants.BabySitterDisabilitiesType);
        old.setClientPrefs(clientPrefs);
        viewModel.saveUser(old);
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
    }

    private void handleClientPrefs(User user) {
        if (user.getClientPrefs() != null && !user.getClientPrefs().isEmpty()) {

            if (user.getClientPrefs().contains(Constants.DogisterType)) {
                mIsDogister = true;
                mIsDogisterBtn.setChecked(true);
            }

            if (user.getClientPrefs().contains(Constants.BabySitterType)) {
                mIsBabysitter = true;
                mIsBabysitterBtn.setChecked(true);
            }

            if (user.getClientPrefs().contains(Constants.BabySitterDisabilitiesType)) {
                mIsBabysitsdisabilities = true;
                mIsBabysitsdisabilitiesBtn.setChecked(true);
            }

            if (user.getClientPrefs().contains(Constants.HouseKeeperType)) {
                mIsHouseKeeper = true;
                mIsHouseKeeperBtn.setChecked(true);
            }

            if (user.getClientPrefs().contains(Constants.TherapistType)) {
                mIsTherapist = true;
                mIsTherapistBtn.setChecked(true);
            }

        }
    }
}