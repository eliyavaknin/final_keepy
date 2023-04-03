package com.keepy.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.keepy.Constants;
import com.keepy.R;
import com.keepy.behaviour.IRequests;
import com.keepy.models.ServiceRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;



/*
 * This class is an adapter for the schedule recycler view
 * We adapt the RequestsRvAdapter to show the schedule of the client or the keeper
 */

public class ScheduleRvAdapter extends RequestsRvAdapter {
    private boolean clientView;

    public ScheduleRvAdapter(List<ServiceRequest> requestList, boolean clientView) {
        super(requestList, null, clientView,false,!clientView, false);
        this.clientView = clientView;
    }


    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // if the user is a client -> use the client view layout
        if (clientView)
            return new ScheduleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item_client, parent, false));
        else // if the user is a keeper -> use the keeper view layout
            return new ScheduleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item_keeper, parent, false));
    }


    // this class is a view holder for the schedule recycler view
    // we do not make it static because we need to access the clientView variable
    // so we need to make sure that the ScheduleRvAdapter is created before this class
    class ScheduleViewHolder extends RequestsViewHolder {

        TextView name, type, date, location,comment;
        TextView status,phone;



        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            // if the user is a client -> use the client view layout
            if (clientView) {
                status = itemView.findViewById(R.id.service_time_schedule_item_client);
                name = itemView.findViewById(R.id.client_name_schedule_item_client);
                type = itemView.findViewById(R.id.service_type_schedule_item_client);
                date = itemView.findViewById(R.id.service_date_schedule_item_client);
                phone = itemView.findViewById(R.id.service_phone_schedule_item_client);
            } else { // if the user is a keeper -> use the keeper view layout
                location = itemView.findViewById(R.id.client_location_schedule_item_keeper);
                name = itemView.findViewById(R.id.client_name_schedule_item_keeper);
                status = itemView.findViewById(R.id.service_time_schedule_item_keeper);
                type = itemView.findViewById(R.id.service_type_schedule_item_keeper);
                date = itemView.findViewById(R.id.service_date_schedule_item_keeper);
                comment = itemView.findViewById(R.id.client_comment_schedule_item_keeper);
                phone = itemView.findViewById(R.id.service_phone_schedule_item_keeper);
            }

        }

        private String removeLastIfNonAlpha(String str) {
            if (!Character.isAlphabetic(str.charAt(str.length() - 1)))
                return str.length() <= 1 ? str: str.substring(0, str.length() - 2);
            return str;
        }

        @SuppressLint("SetTextI18n") // we know that the text is not null @_@
        public void bind(ServiceRequest request,
                         IRequests iRequests) {
            LocalDateTime dateLocal = LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getServiceTime().getDate()), ZoneId.systemDefault());
            String dateStr = dateLocal.getDayOfMonth() + "/" + (dateLocal.getMonthValue() - 1) + "/" + dateLocal.getYear();
            name.setText("Client name: " + request.getClientName());
            type.setText("Service type: " + removeLastIfNonAlpha(request.getType()));
            if (location != null)
                location.setText("Requested service at: " + request.getLocation());
            if (comment != null)
                comment.setText("Client comment: " + request.getClientComment());
            date.setText("date: " +  dateStr + " at " + (request.getServiceTime().getStartHour() >=10 ? request.getServiceTime().getStartHour() : "0" + request.getServiceTime().getStartHour()) + ":" + (request.getServiceTime().getStartMinute() >= 10 ? request.getServiceTime().getStartMinute() : "0" + request.getServiceTime().getStartMinute()));

            if(clientView) {
                phone.setText("Phone: " +((request.getKeeperPhone() == null || request.getKeeperPhone().isEmpty()) ? "Not provided" : request.getKeeperPhone()));
            }else {
                phone.setText("Phone: " + ((request.getClientPhone() == null || request.getClientPhone().isEmpty()) ? "Not provided" : request.getClientPhone()));
            }
            status.setText("Status: " + Constants.Utils.getRequestStatusString(request.getStatus()));
            switch(request.getStatus()) {
                case ServiceRequest.Status.WAITING:
                    int orange = Color.rgb(255, 165, 0);
                    status.setTextColor(orange);
                    break;
                case ServiceRequest.Status.APPROVED:
                    status.setTextColor(Color.GREEN);
                    break;
                case ServiceRequest.Status.DECLINED:
                    status.setTextColor(Color.RED);
                    break;
                case ServiceRequest.Status.FULFILLED:
                    status.setTextColor(Color.GRAY);
                    break;
            }
        }

    }


}
