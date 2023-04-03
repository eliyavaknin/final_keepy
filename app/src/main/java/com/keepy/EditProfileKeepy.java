package com.keepy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.keepy.behaviour.IRequests;
import com.keepy.dialogs.CalendarActivity;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;
import com.keepy.viewmodel.AppViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class EditProfileKeepy extends AppCompatActivity implements IRequests {
    private Button mIsDogisterBtn;
    private Button mIsHouseKeeperBtn;
    private Button mIsTherapistBtn;
    private Button mIsBabysitterBtn;
    private Button mIsBabysitsdisabilitiesBtn;

    ArrayAdapter<String> adapterItems;


    private boolean mIsDogister = false;
    private boolean mIsHouseKeeper = false;
    private boolean mIsTherapist = false;
    private boolean mIsBabysitter = false;
    private boolean mIsBabysitsdisabilities = false;


    private AppViewModel viewModel;

    Spinner location_Edit;
    TextInputEditText nameTv, phoneTv, aboutTv;

    EditText dogisterPrice_et, therapistPrice_et,
            babysitterPrice_et, babysitterDisabilitiesPrice_et,
            houseKeeperPrice_et;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_keepy);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        findViews();
        findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
        });
        location_Edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (viewModel.userLiveData.getValue() != null && location_Edit.getSelectedItem().toString().equals(viewModel.userLiveData.getValue().getmLocation()))
                    return;
                findViewById(R.id.saveChanges_Keeper).setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        nameTv.addTextChangedListener(MainActivity.getProfileTextWatcher(this, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return viewModel.userLiveData.getValue() != null &&
                        s.equals(viewModel.userLiveData.getValue().getmFullName());
            }
        }));
        phoneTv.addTextChangedListener(MainActivity.getProfileTextWatcher(this, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return viewModel.userLiveData.getValue() != null &&
                        s.equals(viewModel.userLiveData.getValue().getmPhone());
            }
        }));
        aboutTv.addTextChangedListener(MainActivity.getProfileTextWatcher(this, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return viewModel.userLiveData.getValue() != null &&
                        s.equals(viewModel.userLiveData.getValue().getmAboutMe());
            }
        }));


        findViewById(R.id.homeBtn).setOnClickListener(v -> finish());
        findViewById(R.id.scheduleBtn).setOnClickListener(v -> openSchedule());

        ProgressDialog dialog = new ProgressDialog(this);

        Button saveChangesBtn = findViewById(R.id.saveChanges_Keeper);
        saveChangesBtn.setEnabled(false);
        saveChangesBtn.setOnClickListener(v -> saveChanges());
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
            if (user == null) {
                return;
            }
            nameTv.setText(user.getmFullName());
            phoneTv.setText(user.getmPhone());
            for (int i = 0; i < Constants.places.length; i++) {
                if (Constants.places[i].equals(user.getmLocation())) {
                    location_Edit.setSelection(i, true);
                    break;
                }
            }
            aboutTv.setText(user.getmAboutMe());
            handleKeeperFees(user);
        });

        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, Constants.places);
        location_Edit.setAdapter(adapterItems);

        mIsDogisterBtn.setOnClickListener(v -> {
            mIsDogister = !mIsDogister;
            saveChangesBtn.setEnabled(true);
            if (mIsDogister) {
                mIsDogisterBtn.setBackgroundColor(Constants.ColorSelected);
                mIsDogisterBtn.setTextColor(Color.WHITE);
                return;
            }
            mIsDogisterBtn.setTextColor(Color.BLACK);
            mIsDogisterBtn.setBackgroundColor(Constants.ColorUnselected);
        });

        mIsHouseKeeperBtn.setOnClickListener(v -> {
            mIsHouseKeeper = !mIsHouseKeeper;
            saveChangesBtn.setEnabled(true);
            if (mIsHouseKeeper) {
                mIsHouseKeeperBtn.setBackgroundColor(Constants.ColorSelected);
                mIsHouseKeeperBtn.setTextColor(Color.WHITE);
                return;
            }
            mIsHouseKeeperBtn.setTextColor(Color.BLACK);
            mIsHouseKeeperBtn.setBackgroundColor(Constants.ColorUnselected);
        });

        mIsTherapistBtn.setOnClickListener(v -> {
            saveChangesBtn.setEnabled(true);
            mIsTherapist = !mIsTherapist;
            if (mIsTherapist) {
                mIsTherapistBtn.setBackgroundColor(Constants.ColorSelected);
                mIsTherapistBtn.setTextColor(Color.WHITE);
                return;
            }
            mIsTherapistBtn.setTextColor(Color.BLACK);
            mIsTherapistBtn.setBackgroundColor(Constants.ColorUnselected);
        });
        mIsBabysitterBtn.setOnClickListener(v -> {
            mIsBabysitter = !mIsBabysitter;
            saveChangesBtn.setEnabled(true);
            if (mIsBabysitter) {
                mIsBabysitterBtn.setBackgroundColor(Constants.ColorSelected);
                mIsBabysitterBtn.setTextColor(Color.WHITE);
                return;
            }
            mIsBabysitterBtn.setTextColor(Color.BLACK);
            mIsBabysitterBtn.setBackgroundColor(Constants.ColorUnselected);
        });

        mIsBabysitsdisabilitiesBtn.setOnClickListener(v -> {
            saveChangesBtn.setEnabled(true);
            mIsBabysitsdisabilities = !mIsBabysitsdisabilities;
            if (mIsBabysitsdisabilities) {
                mIsBabysitsdisabilitiesBtn.setTextColor(Color.WHITE);
                mIsBabysitsdisabilitiesBtn.setBackgroundColor(Constants.ColorSelected);
                return;
            }
            mIsBabysitsdisabilitiesBtn.setTextColor(Color.BLACK);
            mIsBabysitsdisabilitiesBtn.setBackgroundColor(Constants.ColorUnselected);
        });


    }


    private void findViews() {

        location_Edit = findViewById(R.id.locationEdit_Keeper);
        nameTv = findViewById(R.id.FullNameEdit_Keeper);
        phoneTv = findViewById(R.id.PhoneEdit_Keeper);
        aboutTv = findViewById(R.id.AboutMeEdit_Keeper);
        dogisterPrice_et = findViewById(R.id.dogisterPrice_et);
        therapistPrice_et = findViewById(R.id.therapistKeeperPrice_et);
        babysitterPrice_et = findViewById(R.id.babysitterPrice_et);
        babysitterDisabilitiesPrice_et = findViewById(R.id.babysitterDisabilitiesPrice_et);
        houseKeeperPrice_et = findViewById(R.id.houseKeeperPrice_et);

        mIsDogisterBtn = findViewById(R.id.Dogister_KeeperBtn);
        mIsHouseKeeperBtn = findViewById(R.id.HouseKeeper_Keeper_Btn);
        mIsTherapistBtn = findViewById(R.id.Therapist_Keeper_Btn);
        mIsBabysitterBtn = findViewById(R.id.Babysitter_KeeperBtn);
        mIsBabysitsdisabilitiesBtn = findViewById(R.id.Babysitsdisabilities_Keeper_Btn);

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
                false,
                incomingRequests,
                this);
        calendarDialog.show();

    }


    /*
     * This method is used to validate the keeper fees
     * the keeper fees must be non-negative
     */
    private void showToastErrorPrice(String keeperType) {
        Snackbar.make(findViewById(R.id.parentLayoutEditKeepy), keeperType + " price cannot be non-positive!", Snackbar.LENGTH_LONG).show();
    }

    private void showToastPhoneError() {
        Snackbar.make(findViewById(R.id.parentLayoutEditKeepy), "Phone number must be israeli number, 10 digits long!", Snackbar.LENGTH_LONG).show();
    }

    /*
     * This method is used to validate the about me field
     * the about me field must be at-least 4 characters long
     */
    private boolean isValidAbout(String about) {
        if (about.length() < 4) {
            Snackbar.make(findViewById(R.id.parentLayoutEditKeepy), "About me must be at-least 4 characters long!", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
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

    private boolean isValidName(String name) {
        if (name.length() < 2) {
            Snackbar.make(findViewById(R.id.parentLayoutEditKeepy), "Name must be at-least 2 characters long!", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void saveChanges() {
        User editingUser = viewModel.userLiveData.getValue();
        if (editingUser == null) {
            Toast.makeText(this, "Some unknown error has ocured, please try again later", Toast.LENGTH_LONG).show();
            return;
        }

        boolean hasNameTv = nameTv.getText() != null && !TextUtils.isEmpty(nameTv.getText().toString());
        if (hasNameTv) {
            if (!isValidName(nameTv.getText().toString()))
                return;
            String name = nameTv.getText().toString();
            editingUser.setmFullName(name);
        }

        boolean hasPhoneTv = phoneTv.getText() != null && !TextUtils.isEmpty(phoneTv.getText().toString());

        if (hasPhoneTv) {
            if (!isValidPhone(phoneTv.getText().toString()))
                return;
            String phone = phoneTv.getText().toString();
            editingUser.setmPhone(phone);
        }

        String location = Constants.places[location_Edit.getSelectedItemPosition()];
        editingUser.setmLocation(location);

        boolean hasAboutTv = aboutTv.getText() != null && !TextUtils.isEmpty(aboutTv.getText().toString());

        if (hasAboutTv) {
            if (!isValidAbout(aboutTv.getText().toString()))
                return;
            String about = aboutTv.getText().toString();
            editingUser.setmAboutMe(about);
        }

        String dogisterPrice = dogisterPrice_et.getText().toString();
        String babySitterPrice = babysitterPrice_et.getText().toString();
        String babySitterDisabilitiesPrice = babysitterDisabilitiesPrice_et.getText().toString();
        String houseKeeperPrice = houseKeeperPrice_et.getText().toString();
        String therapistPrice = therapistPrice_et.getText().toString();

        HashMap<String, Integer> fees = new HashMap<>();
        /* Ignored exceptions are thrown when the user didn't enter a price for a keeper type */
        if (mIsDogister) {
            try {
                int newDogisterPrice = Integer.parseInt(dogisterPrice);
                fees.put(Constants.DogisterType, newDogisterPrice);
                if (newDogisterPrice <= 0) {
                    showToastErrorPrice(Constants.DogisterType_View);
                    fees.remove(Constants.DogisterType);
                    return;
                }
            } catch (Exception ignored) {
                showToastErrorPrice(Constants.DogisterType_View);
                return;
            }
        }
        if (mIsBabysitter) {
            try {
                int newBabySitterPrice = Integer.parseInt(babySitterPrice);
                fees.put(Constants.BabySitterType, newBabySitterPrice);
                if (newBabySitterPrice <= 0) {
                    showToastErrorPrice(Constants.BabySitterType_View);
                    fees.remove(Constants.BabySitterType);
                    return;
                }
            } catch (Exception ignored) {
                showToastErrorPrice(Constants.BabySitterType_View);
                return;
            }
        }
        if (mIsBabysitsdisabilities) {
            try {
                int newBabySitterDisabilitiesPrice = Integer.parseInt(babySitterDisabilitiesPrice);
                fees.put(Constants.BabySitterDisabilitiesType, newBabySitterDisabilitiesPrice);
                if (newBabySitterDisabilitiesPrice <= 0) {
                    showToastErrorPrice(Constants.BabySitterDisabilitiesType_View);
                    fees.remove(Constants.BabySitterDisabilitiesType);
                    return;
                }
            } catch (Exception ignored) {
                showToastErrorPrice(Constants.BabySitterDisabilitiesType_View);
                return;
            }
        }
        if (mIsHouseKeeper) {
            try {
                int newHouseKeeperPrice = Integer.parseInt(houseKeeperPrice);
                fees.put(Constants.HouseKeeperType, newHouseKeeperPrice);
                if (newHouseKeeperPrice <= 0) {
                    showToastErrorPrice(Constants.HouseKeeperType_View);
                    fees.remove(Constants.HouseKeeperType);
                    return;
                }
            } catch (Exception ignored) {
                showToastErrorPrice(Constants.HouseKeeperType_View);
                return;
            }
        }
        if (mIsTherapist) {
            try {
                int newTherapistPrice = Integer.parseInt(therapistPrice);
                fees.put(Constants.TherapistType, newTherapistPrice);
                if (newTherapistPrice <= 0) {
                    showToastErrorPrice(Constants.TherapistType_View);
                    fees.remove(Constants.TherapistType);
                    return;
                }
            } catch (Exception ignored) {
                showToastErrorPrice(Constants.HouseKeeperType_View);
                return;
            }
        }

        KeeperData data = new KeeperData();
        data.setFees(fees);
        if (editingUser.getmKeeperData() != null)
            data.setRating(editingUser.getmKeeperData().getRating());

        editingUser.setmKeeperData(data);
        viewModel.saveUser(editingUser);
        Button saveChangesBtn = findViewById(R.id.saveChanges_Keeper);
        saveChangesBtn.setEnabled(false);

        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
    }

    private void handleKeeperFees(User user) {
        if (user.getmKeeperData() != null
                && user.getmKeeperData().getFees() != null) {

            HashMap<String, Integer> fees = user.getmKeeperData().getFees();

            if (fees.containsKey(Constants.DogisterType)) {
                Integer dogisterFee = fees.get(Constants.DogisterType);
                dogisterPrice_et.setText(String.valueOf(dogisterFee));
                mIsDogister = true;
                mIsDogisterBtn.setBackgroundColor(Constants.ColorSelected);
                mIsDogisterBtn.setTextColor(Color.WHITE);
            }
            if (fees.containsKey(Constants.BabySitterType)) {
                Integer babysitterFee = fees.get(Constants.BabySitterType);
                babysitterPrice_et.setText(String.valueOf(babysitterFee));
                mIsBabysitter = true;
                mIsBabysitterBtn.setBackgroundColor(Constants.ColorSelected);
                mIsBabysitterBtn.setTextColor(Color.WHITE);
            }
            if (fees.containsKey(Constants.BabySitterDisabilitiesType)) {
                Integer babysitterDisabilitiesFee = fees.get(Constants.BabySitterDisabilitiesType);
                babysitterDisabilitiesPrice_et.setText(String.valueOf(babysitterDisabilitiesFee));
                mIsBabysitsdisabilities = true;
                mIsBabysitsdisabilitiesBtn.setBackgroundColor(Constants.ColorSelected);
                mIsBabysitsdisabilitiesBtn.setTextColor(Color.WHITE);
            }
            if (fees.containsKey(Constants.HouseKeeperType)) {
                Integer houseKeeperFees = fees.get(Constants.HouseKeeperType);
                houseKeeperPrice_et.setText(String.valueOf(houseKeeperFees));
                mIsHouseKeeper = true;
                mIsHouseKeeperBtn.setBackgroundColor(Constants.ColorSelected);
                mIsHouseKeeperBtn.setTextColor(Color.WHITE);
            }
            if (fees.containsKey(Constants.TherapistType)) {
                Integer therapistFees = fees.get(Constants.TherapistType);
                therapistPrice_et.setText(String.valueOf(therapistFees));
                mIsTherapist = true;
                mIsTherapistBtn.setBackgroundColor(Constants.ColorSelected);
                mIsTherapistBtn.setTextColor(Color.WHITE);
            }
        }
    }

    // This method is used to approve a request.
    // It is called when the user clicks on the "Approve" button.
    // It calls the approveRequest method from the view model.
    @Override
    public void onApprove(ServiceRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Approve Request");
        builder.setMessage("Are you sure you want to approve this request?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            viewModel.approveRequest(request);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }


    // This method is used to decline a request.
    // It is called when the user clicks on the "Decline" button.
    // It calls the declineRequest method from the view model.
    @Override
    public void onDecline(ServiceRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Decline Request");
        builder.setMessage("Are you sure you want to decline this request?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            viewModel.declineRequest(request);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    public void onCanceled(ServiceRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Request");
        builder.setMessage("Are you sure you want to cancel this request?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            viewModel.cancelRequest(request);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
}