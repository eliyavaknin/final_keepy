package com.keepy.behaviour;


import com.keepy.models.ServiceRequest;

/*
 * This interface is used to handle the approve and decline buttons
 * We use it in the RequestsActivity and the ScheduleActivity
 * We use it to approve or decline the requests of the client or the keeper
 */
public interface IRequests {
    void onApprove(ServiceRequest request);

    void onDecline(ServiceRequest request);

    void onCanceled(ServiceRequest request);
}