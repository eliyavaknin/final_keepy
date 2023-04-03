package com.keepy.behaviour;

import com.keepy.models.ServiceRequest;
import com.keepy.models.User;


/*
    * This interface is used to send requests to the keepers
    * We use it to send requests to the keepers
    * We use it to rate the keepers
    * We use it to close the dialog
 */
public interface IKeeperProfile {
    void sendRequest(User client, User keeper,
                     ServiceRequest serviceRequest);

    void rateMe(User client, User keeper, float rating);

    void close();

}