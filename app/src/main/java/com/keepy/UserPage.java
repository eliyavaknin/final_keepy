package com.keepy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.keepy.adapters.CustomArrayAdapter;
import com.keepy.adapters.RequestsRvAdapter;
import com.keepy.behaviour.IRequests;
import com.keepy.behaviour.ISearchResultsDialog;
import com.keepy.dialogs.CalendarActivity;
import com.keepy.dialogs.SearchResultsDialog;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;
import com.keepy.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPage extends AppCompatActivity implements IRequests {

    private AppViewModel viewModel;

    // indicates which view is currently displayed // --- // ----------------------------------------- //
    private boolean isClientView = true;
    private boolean isKeeperView = true;
    private boolean isBothView = true;
    // ----------------------------------------- // --- // ----------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        setContentView(R.layout.activity_user);
        ProgressDialog dialog = new ProgressDialog(this);


        LinearLayout keeper_Btn_layout = findViewById(R.id.keeper_Btn_layout);
        LinearLayout client_Btn_layout = findViewById(R.id.client_Btn_layout);

        Button wantToBecomeKeeper = findViewById(R.id.keeper_Btn_add);
        wantToBecomeKeeper.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to become a keeper?");
            builder.setMessage("You will be able to see all the requests and you will be able to accept them");
            builder.setPositiveButton("Yes", (dialog1, which) -> {
                viewModel.wantToBecomeKeeper();
                Toast.makeText(this,
                        "You are now both a client and a keeper"
                        , Toast.LENGTH_SHORT).show();
                keeper_Btn_layout.setVisibility(View.GONE);
            });
            builder.setNegativeButton("No", (dialog1, which) -> {
                dialog1.dismiss();
            });
            builder.show();
        });
        Button wantToBecomeClient = findViewById(R.id.client_Btn_add);

        wantToBecomeClient.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to become a client?");
            builder.setMessage("You will be able to see all the keepers and you will be able to send them requests");
            builder.setPositiveButton("Yes", (dialog12, which) -> {
                viewModel.wantToBecomeClient();
                Toast.makeText(this,
                        "You are now both a client and a keeper"
                        , Toast.LENGTH_SHORT).show();
                client_Btn_layout.setVisibility(View.GONE);
            });
            builder.setNegativeButton("No", (dialog1, which) -> {
                dialog1.dismiss();
            });
            builder.show();
        });
        findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
        });
        findViewById(R.id.profileBtn).setOnClickListener(v ->
                openEditProfile());
        findViewById(R.id.settingsBtn).setOnClickListener(v ->
                openSettingPage());
        findViewById(R.id.scheduleBtn).setOnClickListener(v ->
                openSchedule());


        Button switchViewToClient = findViewById(R.id.client_Btn);
        Button switchViewToKeeper = findViewById(R.id.keeper_Btn);
        RecyclerView clientRequests = findViewById(R.id.rvRequests);

        //  RecyclerView keeperScheduling = findViewById(R.id.rvKeeperRequests);
        clientRequests.setLayoutManager(new LinearLayoutManager(this));
        viewModel.incomingRequests.observe(this, requests -> {
            if (requests != null && requests.size() > 0) {
                clientRequests.setAdapter(new RequestsRvAdapter(requests,
                        UserPage.this,
                        false,
                        true,
                        true,
                        false));
            } else {
                TextView noRequests = findViewById(R.id.tvtopx);
                noRequests.setVisibility(View.VISIBLE);
            }
        });

        switchViewToClient.setOnClickListener(v -> {
            switchClientView();
            v.setBackgroundColor(Constants.ColorSelected);
            switchViewToKeeper.setBackgroundColor(Constants.ColorUnselected);
        });
        switchViewToKeeper.setOnClickListener(v -> {
            switchKeeperView();
            v.setBackgroundColor(Constants.ColorSelected);
            switchViewToClient.setBackgroundColor(Constants.ColorUnselected);
        });


        // observe changes on the user's data

        viewModel.userLiveData.observe(this, user -> {
            if (user != null) {
                if (user.getmIsKeeper() && user.getmIsClient()) {
                    switchBothView();
                } else if (user.getmIsClient()) {
                    switchClientView();
                    keeper_Btn_layout.setVisibility(View.VISIBLE);
                } else {
                    switchKeeperView();
                    client_Btn_layout.setVisibility(View.VISIBLE);
                }
                if (user.getmIsClient()) {
                    attachClientSearchButtons();
                }
            }
        });

        // observe changes on the loading data
        viewModel.loadingLivaData.observe(this, isLoading -> {
            if (isLoading != null) {
                dialog.setTitle(Constants.AppName);
                dialog.setMessage(isLoading);
                dialog.show();
                return;
            }
            dialog.dismiss();
        });
        FirebaseAuth.getInstance()
                .addAuthStateListener(firebaseAuth -> {
                    if (firebaseAuth.getCurrentUser() == null) {
                        openMainPage();
                    }
                });

    }

    SearchResultsDialog dialog_rec;

    // this method is used to attach the client's search buttons
    public void attachClientSearchButtons() {
        List<String> keeperTypes = Arrays.asList(
                "Select Keeper Type",
                Constants.Utils.getKeeperNameAsAppropriateString(Constants.DogisterType),
                Constants.Utils.getKeeperNameAsAppropriateString(Constants.TherapistType),
                Constants.Utils.getKeeperNameAsAppropriateString(Constants.BabySitterType),
                Constants.Utils.getKeeperNameAsAppropriateString(Constants.BabySitterDisabilitiesType),
                Constants.Utils.getKeeperNameAsAppropriateString(Constants.HouseKeeperType)
        );
        ArrayAdapter<String> adapter = new CustomArrayAdapter(this, android.R.layout.simple_spinner_item, keeperTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner keeperTypeSpinner = findViewById(R.id.DomainEdit_ClientSearch);
        keeperTypeSpinner.setAdapter(adapter);
        keeperTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    return;
                viewModel.toggleSearchKeeperType(
                        Constants.Utils.getKeeperTypeFromAppropriateString(keeperTypes.get(position))
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner locationEditClientSearch = findViewById(R.id.locationEdit_ClientSearch);

        Button searchSubmit = findViewById(R.id.ClientSearchBtn);


        searchSubmit.setOnClickListener(v -> {
            if (viewModel.getSearchKeeperTypes().size() == 0) {
                Snackbar.make(v, "Please select at least one keeper type", Snackbar.LENGTH_LONG).show();
            } else {
                openSearchResults();
            }
        });

        // add the locations to the spinner
        ArrayList<String> places = new ArrayList<>(Arrays.asList(Constants.places));
        places.add(0, "All");
        ArrayAdapter<String> adapterItems = new ArrayAdapter<>(this, R.layout.list_item, places);
        locationEditClientSearch.setAdapter(adapterItems);
        locationEditClientSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setSearchLocation(places.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        /*
         * observe changes on the search results
         * and open the search results dialog with the results
         */
        viewModel.keepersByType.observe(this, keepers -> {
            if (keepers != null && dialog_rec == null) {
                User client = viewModel.userLiveData.getValue();
                dialog_rec = new SearchResultsDialog(this, client,
                        keepers,
                        viewModel.outgoingRequests.getValue(),
                        this,
                        new ISearchResultsDialog() {
                            @Override
                            public void close() {
                                viewModel.clearSearchValues();
                                dialog_rec.dismiss();
                            }

                            @Override
                            public void sendRequest(User client, User keeper, ServiceRequest serviceRequest) {
                                viewModel.sendRequest(client, keeper, serviceRequest);
                            }

                            @Override
                            public void rateMe(User client, User keeper, float rating) {
                                viewModel.rateKeeper(client, keeper, rating);
                            }
                        });
                dialog_rec.show();
                dialog_rec.setOnCancelListener(dialog -> dialog_rec = null);
                dialog_rec.setOnDismissListener(dialog -> dialog_rec = null);
            }
        });

    }

    void openSearchResults() {
        viewModel.searchKeepers();
    }

    void openRecommendations() {
        viewModel.searchKeepers();
    }


    /*
     * This method is used to switch the view to the both view.
     */
    private void switchClientView() {
        View clientView = findViewById(R.id.client_screen);
        View keeperView = findViewById(R.id.keeper_screen);

        clientView.setVisibility(View.VISIBLE);
        keeperView.setVisibility(View.GONE);

        this.isClientView = true;
        this.isBothView = false;
        this.isKeeperView = false;
    }


    //  This method is used to switch the view to the keeper view.
    //  It is called when the user clicks on the "I'm a keeper" button.
    //  It hides the client view and shows the keeper view.
    private void switchKeeperView() {
        View clientView = findViewById(R.id.client_screen);
        View keeperView = findViewById(R.id.keeper_screen);
        keeperView.setVisibility(View.VISIBLE);
        clientView.setVisibility(View.GONE);


        this.isClientView = false;
        this.isBothView = false;
        this.isKeeperView = true;

    }


    // This method is used to switch the view to the both view.
    // It is called when the user clicks on the "I'm both" button.
    // It hides the client view and shows the keeper view.
    private void switchBothView() {
        LinearLayout switchView = findViewById(R.id.switch_user);
        switchView.setVisibility(View.VISIBLE);

        this.isClientView = false;
        this.isBothView = true;
        this.isKeeperView = false;
    }


    public void openMainPage() {

        Intent intent = new Intent(UserPage.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    CalendarActivity calendarDialog;

    // This method is used to open the schedule activity.
    // It is called when the user clicks on the "My Schedule" button.
    // It opens the schedule activity as a dialog.
    public void openSchedule() {
        User user = viewModel.userLiveData.getValue();
        List<ServiceRequest> incomingRequests = viewModel.incomingRequests.getValue();
        List<ServiceRequest> outgoingRequests = viewModel.outgoingRequests.getValue();
        if (user == null || (incomingRequests == null && outgoingRequests == null)) {
            Snackbar.make(findViewById(R.id.mainLayout), "You need to be logged in to use these features", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (((incomingRequests == null
                || incomingRequests.size() == 0) && user.getmIsClient() && isClientView)
                && ((outgoingRequests == null
                || outgoingRequests.size() == 0) && user.getmIsKeeper() && isKeeperView)) {
            Snackbar.make(findViewById(R.id.mainLayout), "You have no scheduled services", Snackbar.LENGTH_LONG).show();
            return;
        }

        calendarDialog = new CalendarActivity(
                this,
                viewModel,
                user.getmIsClient() && (isClientView || isBothView),
                user.getmIsKeeper() && (isKeeperView) ? incomingRequests : outgoingRequests,
                this);
        calendarDialog.show();
    }


    // This method is used to open the edit profile activity.
    // It is called when the user clicks on the "Profile" button.
    // It opens the edit profile activity.
    public void openEditProfile() {
        User user = viewModel.userLiveData.getValue();

        if (user == null) {
            Snackbar.make(findViewById(R.id.mainLayout), "You need to be logged in to use these features", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (isClientView || isBothView) {
            Intent intent = new Intent(UserPage.this, EditProfileClient.class);
            startActivity(intent);
        } else if (isKeeperView) {
            Intent intent = new Intent(UserPage.this, EditProfileKeepy.class);
            startActivity(intent);
        }

    }
    public void openSettingPage() {
        Intent Intent = new Intent(UserPage.this, Setting.class);
        startActivity(Intent);
    }

    // ---- May be used by other displayed activities ----
    public boolean isBothView() {
        return isBothView;
    }

    public boolean isClientView() {
        return isClientView;
    }

    public boolean isKeeperView() {
        return isKeeperView;
    }
    // ---- ------- ----

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
            calendarDialog.dismiss();
            if (Constants.Utils.tempDialogPtr != null)
                Constants.Utils.tempDialogPtr.dismiss();
            Toast.makeText(this, "Request canceled", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
            if (Constants.Utils.tempDialogPtr != null)
                Constants.Utils.tempDialogPtr.dismiss();
        });
        builder.show();
    }

}


