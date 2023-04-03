package com.keepy.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.keepy.R;
import com.keepy.behaviour.IRequests;
import com.keepy.models.ServiceRequest;

import java.util.ArrayList;
import java.util.List;


/*
 * This class is an adapter for the requests recycler view
 * We use it in the RequestsActivity and the ScheduleActivity
 * We use it to show the requests of the client or the keeper
 */
public class RequestsRvAdapter extends RecyclerView.Adapter<RequestsRvAdapter.RequestsViewHolder> {

    private final List<ServiceRequest> requestList;


    private final IRequests iRequests;
    private boolean isRequestPreSend;
    private boolean isClientView;

    public RequestsRvAdapter(List<ServiceRequest> requestList,
                             IRequests iRequests,
                             boolean isClientView,
                             boolean waitOnly,
                             boolean isRequestPreSend,
                             boolean approvedOnly) {
        this.isRequestPreSend = isRequestPreSend;
        this.isClientView = isClientView;

        // if we want to show only the waiting requests
        if (waitOnly) {
            ArrayList<ServiceRequest> reqListFiltered = new ArrayList<>();
            for (ServiceRequest request : requestList) {
                if (request.getStatus() == ServiceRequest.Status.WAITING)
                    reqListFiltered.add(request);
            }
            this.requestList = reqListFiltered;
        } else if (approvedOnly) { // if we want to show only the approved requests
            ArrayList<ServiceRequest> reqListFiltered = new ArrayList<>();
            for (ServiceRequest request : requestList) {
                if (request.getStatus() == ServiceRequest.Status.APPROVED)
                    reqListFiltered.add(request);
            }
            this.requestList = reqListFiltered;
        } else { // if we want to show all the requests
            this.requestList = requestList;
        }
        this.iRequests = iRequests;
    }


    // this class is a view holder for the requests recycler view
    // we make it static because we do not need to access any variable from the RequestsRvAdapter
    class RequestsViewHolder extends RecyclerView.ViewHolder {

        private final TextView name, type, comment, location;

        private final Button approve, decline;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_request);
            type = itemView.findViewById(R.id.type_of_service_request);
            comment = itemView.findViewById(R.id.comment_request);
            approve = itemView.findViewById(R.id.approve_request);
            decline = itemView.findViewById(R.id.decline_request);
            location = itemView.findViewById(R.id.location_request);
            if (!isRequestPreSend && !isClientView) {
                decline.setVisibility(View.GONE);
                approve.setBackgroundColor(Color.RED);
            }
            if(isClientView && decline != null){
                decline.setVisibility(View.GONE);
            }
        }


        private String removeLastIfNonAlpha(String str) {
            if (!Character.isAlphabetic(str.charAt(str.length() - 1)))
                return str.length() <= 1 ? str : str.substring(0, str.length() - 2);
            return str;
        }

        public void bind(ServiceRequest request,
                         IRequests iRequests) {
            if(isClientView)
                name.setText("Keeper name: " + (request.getKeeperName().isEmpty() ? "Anonymous" : request.getKeeperName()));
            else
                name.setText("Client name: " + request.getClientName());
            type.setText("Service type: " + removeLastIfNonAlpha(request.getType()));
            location.setText("Requested service at: " + request.getLocation());

            if(request.getClientComment() == null)
                if(isClientView)
                    comment.setText("My comment: No comment");
                else
                    comment.setText("Client comment: No comment from client");
            else {
                if (isClientView) {
                    comment.setText("My comment: " + request.getClientComment());
                } else {
                    comment.setText("Client comment: " + request.getClientComment());
                }
            }
            if(!isRequestPreSend) {
                approve.setText(isClientView ? "Cancel request" : "Cancel service");
                approve.setOnClickListener(v -> iRequests.onCanceled(request));
            }else {
                approve.setOnClickListener(v -> iRequests.onApprove(request));
            }
            decline.setOnClickListener(v -> iRequests.onDecline(request));
        }
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new RequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolder holder, int position) {
        ServiceRequest request = requestList.get(position);
        holder.bind(request, iRequests);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

}
