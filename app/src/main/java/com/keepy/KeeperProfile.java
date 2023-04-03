package com.keepy;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.keepy.behaviour.IKeeperProfile;
import com.keepy.behaviour.IRequests;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class KeeperProfile extends AlertDialog {
    private String removeLastIfNonAlpha(String str) {
        if (!Character.isAlphabetic(str.charAt(str.length() - 1)))
            return str.length() <= 1 ? str : str.substring(0, str.length() - 2);
        return str;
    }

    private ServiceRequest serviceRequest;

    private final User keeper;
    private final User client;
    private final IKeeperProfile iKeeper;

    private void submitServiceRequest(
            ServiceRequest request
    ) {
        serviceRequest.setClientId(client.getmEmail());
        serviceRequest.setKeeperId(keeper.getmUserID());
        serviceRequest.setLocation(client.getmLocation());
        serviceRequest.setRequestDate(System.currentTimeMillis());
        serviceRequest.setClientEmail(client.getmEmail());
        serviceRequest.setKeeperPhone(keeper.getmPhone());
        serviceRequest.setClientPhone(client.getmPhone());
        serviceRequest.setKeeperEmail(keeper.getmEmail());
        serviceRequest.setClientName(client.getmFullName());
        serviceRequest.setKeeperName(keeper.getmFullName());
        serviceRequest.setStatus(ServiceRequest.Status.WAITING);
        String type = "";
        Set<String> keys = keeper.getmKeeperData().getFees().keySet();
        int i = 0;

        // if the client has preferences
        // then the type of the request
        // will be the first preference
        if (client.getClientPrefs() != null) {
            for (String clientPref : client.getClientPrefs()) {
                if (keys.contains(clientPref)) {
                    type += clientPref;
                    if (i != keys.size() - 1)
                        type += ", ";
                }
                i++;
            }
            serviceRequest.setType(
                    type.equals("") ?
                            keys.toArray()[0].toString() :
                            type
            );
        } else {
            serviceRequest.setType(keys.toArray()[0].toString());
        }
        iKeeper.sendRequest(client, keeper, serviceRequest);
    }

    public KeeperProfile(@NonNull Context context,
                         User client,
                         User keeper,
                         List<ServiceRequest> existingRequestsList,
                         IRequests requestsCallback,
                         IKeeperProfile iKeeper) {

        super(context);
        this.iKeeper = iKeeper;
        this.client = client;
        this.keeper = keeper;
        this.serviceRequest = new ServiceRequest();
        View v = getLayoutInflater().inflate(R.layout.keeper_profile, null);
        setView(v);
        setButton(BUTTON_POSITIVE, "Close", (dialog, which) -> {
            dismiss();
        });

        TextView name = v.findViewById(R.id.keeper_profile_name);
        LinearLayout serviceTypesTv = v.findViewById(R.id.keeper_profile_services);
        TextView location = v.findViewById(R.id.keeper_profile_locations_of_operation);
        TextView about = v.findViewById(R.id.keeper_profile_about);
        RatingBar rating = v.findViewById(R.id.keeper_profile_rating);


        Button sendRequestButton = v.findViewById(R.id.keeper_profile_send_request);
        TextView already_rated = v.findViewById(R.id.already_rated);
        RatingBar rateMe = v.findViewById(R.id.keeper_profile_submit_rating);
        name.setText(keeper.getmFullName().isEmpty() ? "Annonymous" : keeper.getmFullName());
        HashMap<String, Integer> serviceTypes = keeper.getmKeeperData().getFees();

        for (Map.Entry<String, Integer> entry : serviceTypes.entrySet()) {
            TextView type = new TextView(context);
            type.setText(removeLastIfNonAlpha(entry.getKey()) + " - " + entry.getValue() + "₪");
            serviceTypesTv.addView(type);
        }

        if (!keeper.getmAboutMe().isEmpty()) {
            about.setVisibility(View.VISIBLE);
            about.setText(keeper.getmAboutMe());
        }

        location.setText(keeper.getmLocation());
        rating.setRating(keeper.getmKeeperData().getRating());

        if (client.getRatedKeepers().contains(keeper.getmEmail())) {
            already_rated.setVisibility(View.VISIBLE);
            rateMe.setVisibility(View.GONE);
        }
        else if(isBeforeServiceDate(serviceRequest.getServiceTime())) {
            already_rated.setEnabled(false);
            already_rated.setText("Rating can only be submitted after the service date");
            already_rated.setVisibility(View.VISIBLE);
        }
        boolean hasSentRequest = false;
        for (ServiceRequest sr : existingRequestsList)
            if (sr.getKeeperEmail().equals(keeper.getmEmail())) {
                sendRequestButton.setEnabled(false);
                sendRequestButton.setText("Request sent");
                hasSentRequest = true;
                break;
            }
        rateMe.setEnabled(hasSentRequest);
        rateMe.setOnRatingBarChangeListener((ratingBar, v1, b) -> {
            iKeeper.rateMe(client, keeper, v1);
            rateMe.setVisibility(View.GONE);
            already_rated.setVisibility(View.VISIBLE);
            rating.setRating(
                    (rating.getRating() * client.getRatedKeepers().size() + v1) /
                            (client.getRatedKeepers().size() + 1)
            );
            Toast.makeText(context,
                    "Thank you for rating " + keeper.getmFullName() + "!",
                    Toast.LENGTH_SHORT).show();
        });

        for (ServiceRequest sr : existingRequestsList)
            if (sr.getKeeperEmail().equals(keeper.getmEmail())) {
                sendRequestButton.setEnabled(false);
                sendRequestButton.setText("Request sent");
                return;
            }

        sendRequestButton.setOnClickListener(v1 -> {
            sendRequest(context, sendRequestButton,
                    serviceTime -> serviceRequest.setServiceTime(serviceTime),
                    this::submitServiceRequest,
                    serviceRequest,
                    client,
                    keeper,
                    requestsCallback,
                    iKeeper);
        });
    }

    private boolean isBeforeServiceDate(ServiceTime serviceTime) {
        if (serviceTime == null)
            return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(serviceTime.getDate());
        calendar.add(Calendar.HOUR, -1);
        return calendar.getTimeInMillis() < System.currentTimeMillis();
    }


    public static void sendRequest(Context context,
                                   Button requestButton,
                                   OnSuccessListener<ServiceTime> timeSelected,
                                   OnSuccessListener<ServiceRequest> onSendRequest,
                                   ServiceRequest request,
                                   User client,
                                   User keeper,
                                   IRequests requestsCallback,
                                   IKeeperProfile iKeeper) {
        openDialogAddCommentToRequest(context, client, keeper, iKeeper,requestsCallback, requestButton, request, timeSelected,onSendRequest);
    }


    private static void getKeeperRequestList(User keeper,
                                             OnSuccessListener<List<ServiceRequest>> requestsListener) {
        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection(Constants.UsersCollection)
                .document(keeper.getmUserID());
        userRef.collection(Constants.ServiceRequestsCollection_incoming)
                .get()
                .addOnSuccessListener(snap -> {
                    List<ServiceRequest> requests;
                    if (snap == null || snap.isEmpty()) {
                        requests = new ArrayList<>();
                    } else {
                        requests = snap.toObjects(ServiceRequest.class);
                    }
                    requestsListener.onSuccess(requests);
                })
                .addOnFailureListener(e -> requestsListener.onSuccess(new ArrayList<>()));
    }


    // method used to open dialog for adding comment to request
    // and submit send request to keeper
    public static void openDialogAddCommentToRequest(
            final Context context,
            final User client,
            final User keeper,
            final IKeeperProfile iKeeper,
            final IRequests requestsCallback,
            final Button requestButton,
            final ServiceRequest request,
            final OnSuccessListener<ServiceTime> timeSelected,
            final OnSuccessListener<ServiceRequest> onSendRequest) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        getKeeperRequestList(keeper, requests -> {
            AddCommentToRequestDialog addCommentToRequestDialog = new AddCommentToRequestDialog(
                    context,
                    requests,
                    request,
                    client,
                    keeper,
                    iKeeper,
                    requestsCallback,
                    requestButton,
                    timeSelected,
                    onSendRequest);
            progressDialog.dismiss();
            addCommentToRequestDialog.show();
        });

    }


    static class AddCommentToRequestDialog extends android.app.AlertDialog {
        private Button requestButton;
        private User keeper;
        private User client;
        private final TextView et;

        private IKeeperProfile iKeeper;

        private final ServiceRequest request;

        public AddCommentToRequestDialog(
                Context context,
                List<ServiceRequest> existingRequestsList,
                ServiceRequest request,
                User client,
                User keeper,
                IKeeperProfile iKeeper,
                IRequests requestsCallback,
                Button requestButton,
                OnSuccessListener<ServiceTime> timeSelected,
                OnSuccessListener<ServiceRequest> onSendRequest) {
            super(context);
            this.iKeeper = iKeeper;
            this.client = client;
            this.request = request;
            AddCommentToRequestDialog.this.setTitle("Keepy");
            AddCommentToRequestDialog.this.setMessage("Leave blank if you don't want to add a comment");

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            LinearLayout ll = new LinearLayout(context);
            ll.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            TableLayout tableLayout = new TableLayout(context);
            tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            ll.addView(tableLayout);
            if (existingRequestsList == null)
                existingRequestsList = new ArrayList<>();
            Constants.Utils.clearSelectedDate();
            Constants.Utils.calendarUI(
                    existingRequestsList,
                    true,
                    true,
                    client,
                    keeper,
                    iKeeper,
                    requestsCallback,
                    timeSelected,
                    context,
                    tableLayout,
                    year,
                    month,
                    Constants.PrefLang);


            ll.setOrientation(LinearLayout.VERTICAL);
            et = new EditText(context);
            et.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            int padding = 4;
            ll.setPadding(padding, padding, padding, padding);
            et.setHint("Enter comment here");
            ll.addView(et);
            AddCommentToRequestDialog.this.setView(ll);
            AddCommentToRequestDialog.this.setButton(BUTTON_POSITIVE, "Send", (dialog, which) -> {
                onCommentAdded(et.getText().toString(), updatedRequest -> {
                    AddCommentToRequestDialog.this.dismiss();
                    onSendRequest.onSuccess(updatedRequest);
                    Toast.makeText(context, "Request sent successfully, you will be contacted by the keeper!", Toast.LENGTH_SHORT).show();
                });
            });
            AddCommentToRequestDialog.this.setButton(BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
                AddCommentToRequestDialog.this.dismiss();
            });
            this.requestButton = requestButton;
            this.keeper = keeper;
        }


        public void onCommentAdded(
                String comment,
                OnSuccessListener<ServiceRequest> onSuccess) {
            if (comment.length() > 0) // if comment was added
                request.setClientComment(comment);
            request.setStatus(ServiceRequest.Status.WAITING);
            if (requestButton != null) {
                requestButton.setEnabled(false);
                requestButton.setText("Request sent");
            }
            Toast.makeText(
                    requestButton.getContext(),
                    "Request sent successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            // clean here to avoid memory leaks
            // in nested classes
            requestButton = null;
            keeper = null;
            client = null;
            AddCommentToRequestDialog.this.dismiss();
            onSuccess.onSuccess(request);
            iKeeper.close();
            iKeeper = null;
        }
    }


}
