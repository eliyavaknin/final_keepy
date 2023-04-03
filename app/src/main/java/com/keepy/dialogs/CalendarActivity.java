package com.keepy.dialogs;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.keepy.Constants;
import com.keepy.R;
import com.keepy.adapters.ScheduleRvAdapter;
import com.keepy.behaviour.IKeeperProfile;
import com.keepy.behaviour.IRequests;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;
import com.keepy.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * <p>
 * This class is used to create a calendar dialog
 * that can be used to select dates.
 * to show booked dates.
 * </p>
 */
public class CalendarActivity extends AlertDialog implements IKeeperProfile {
    boolean showingCalendar = true;

    View calendarView, appointmentsView;
    LinearLayout layout;
    Button changeViewButton;

    private IRequests iRequests;

    private List<ServiceRequest> requestList;
    private boolean doDismiss = false;

    public CalendarActivity(Activity activity,
                            AppViewModel viewModel,
                            boolean isClientView,
                            List<ServiceRequest> requestList,
                            IRequests iRequests
    ) {
        super(activity);
        layout = new LinearLayout(activity);
        this.iRequests = iRequests;
        this.requestList = requestList;
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        changeViewButton = this.changeViewButton(isClientView, activity);
        layout.addView(changeViewButton);
        setView(layout);

        init(activity, requestList, isClientView, viewModel);
    }

    private void init(
            Activity activity,
            List<ServiceRequest> requestList,
            boolean isClientView,
            AppViewModel viewModel
    ) {
        TableLayout calendarLayout = new TableLayout(activity);
        calendarLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT
        ));

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (requestList == null || requestList.isEmpty()) {
            Toast.makeText(activity, "No requests found", Toast.LENGTH_SHORT).show();
            doDismiss = true;
            requestList = new ArrayList<>();
        }
        List<ServiceRequest> approvedRequests = new ArrayList<>();
        for (ServiceRequest request : requestList) {
            if (request.getStatus() == ServiceRequest.Status.APPROVED) {
                approvedRequests.add(request);
            }
        }
        Constants.Utils.calendarUI(approvedRequests, false, isClientView, null, viewModel.userLiveData.getValue(), this, iRequests, null, activity, calendarLayout, year, month, Constants.PrefLang);
        layout.addView(calendarLayout);
        setButton(BUTTON_POSITIVE, "CLOSE", (dialog, which) -> {
            dialog.dismiss();
        });
        setMessage("No Scheduled Services");
        calendarView = calendarLayout;

        LinearLayout view = (LinearLayout) LayoutInflater.from(activity)
                .inflate(R.layout.activity_schedule, null, false);

        if (requestList.size() == 0) {
            setMessage("No Scheduled Services");
        } else {
            RecyclerView rv = view.findViewById(R.id.scheduleRv);
            rv.setLayoutManager(new LinearLayoutManager(activity));
            rv.setAdapter(new ScheduleRvAdapter(
                    requestList,
                    isClientView
            ));
            layout.addView(view);
        }
        appointmentsView = view;
        if (requestList.isEmpty())
            changeViewButton.setVisibility(View.GONE);
        appointmentsView.setVisibility(View.GONE);
    }

    private Button changeViewButton(
            boolean isClientView,
            Context context
    ) {
        Button b = new Button(context);
        b.setAllCaps(false);
        b.setBackgroundColor(context.getResources().getColor(R.color.lavender));
        b.setTextColor(context.getResources().getColor(R.color.white));
        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        b.setTextSize(18f);
        paramsButton.setMargins(72, 0, 72, 0);
        b.setLayoutParams(paramsButton);
        b.setText("Show services & requests");
        b.setOnClickListener(v -> changeView(isClientView, v));
        return b;
    }

    private void changeView(
            boolean isClientView,
            View v) {
        if (showingCalendar) {
            changeAppointmentsView();

            changeViewButton.setText("Show calendar");
        } else {
            changeCalendarView();
            if (isClientView)
                changeViewButton.setText("Show services & requests");

            else
                changeViewButton.setText("Show requests");
        }
        setShowingCalendar(!showingCalendar);
    }

    private void setShowingCalendar(boolean showingCalendar) {
        this.showingCalendar = showingCalendar;
    }

    private boolean isShowingCalendar() {
        return showingCalendar;
    }


    private void changeCalendarView() {
        if (showingCalendar) return;
        if (appointmentsView != null)
            appointmentsView.setVisibility(View.GONE);
        if (calendarView != null) {
            calendarView.setVisibility(View.VISIBLE);
        }
    }

    public void changeAppointmentsView() {
        if (!showingCalendar) return;
        if (calendarView != null)
            calendarView.setVisibility(View.GONE);
        if (appointmentsView != null) {
            appointmentsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void sendRequest(User client, User keeper, ServiceRequest serviceRequest) {
        // do nothing
    }

    @Override
    public void rateMe(User client, User keeper, float rating) {
        // do nothing
    }

    @Override
    public void close() {
        dismiss();
    }
}
