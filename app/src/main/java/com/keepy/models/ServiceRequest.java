package com.keepy.models;

import com.keepy.Constants;
import com.keepy.ServiceTime;

/*
    * This class is used to store the data of a service request
 */
public class ServiceRequest {


    public String getKeeperPhone() {
        return keeperPhone;
    }
    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public void setKeeperPhone(String keeperPhone) {
        this.keeperPhone = keeperPhone;
    }

    private String keeperPhone;
    private String clientPhone;

    public static class Status {
        public static final int WAITING = 0;
        public static final int APPROVED = 1;
        public static final int DECLINED = 2;
        public static final int FULFILLED = 3;
    }

    private String id;
    private String location;
    private String clientEmail;
    private String keeperEmail;
    private String clientName;
    private String keeperName;
    private String clientId;
    private String keeperId;
    private long date;
    private long requestDate;

    private ServiceTime serviceTime = Constants.Utils.getDefaultServiceTime();

    private String type;
    private String clientComment;
    private int status;


    public ServiceRequest(String clientId, String keeperId, long date, long requestDate, String clientComment, int status,
                          String type,String location, String clientName, String keeperName,
                          String clientEmail, String keeperEmail,
                          String clientPhone, String keeperPhone) {
        this.clientId = clientId;
        this.keeperId = keeperId;
        this.clientPhone = clientPhone;
        this.keeperPhone = keeperPhone;
        this.clientEmail = clientEmail;
        this.keeperEmail = keeperEmail;
        this.location = location;
        this.clientName = clientName;
        this.type = type;
        this.keeperName = keeperName;
        this.date = date;
        this.clientComment = clientComment;
        this.requestDate = requestDate;
        this.status = status;
        this.id = clientId + keeperId + date;
    }

    public ServiceRequest() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getKeeperEmail() {
        return keeperEmail;
    }

    public void setKeeperEmail(String keeperEmail) {
        this.keeperEmail = keeperEmail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClientName() {
        return clientName;
    }

    public String getKeeperName() {
        return keeperName;
    }

    public void setKeeperName(String keeperName) {
        this.keeperName = keeperName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientComment() {
        return clientComment;
    }

    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getKeeperId() {
        return keeperId;
    }

    public void setKeeperId(String keeperId) {
        this.keeperId = keeperId;
    }

    public long getDate() {
        return date;
    }




    public void setDate(long date) {
        this.date = date;
    }


    public long getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(long requestDate) {
        this.requestDate = requestDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ServiceTime getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(ServiceTime serviceTime) {
        this.serviceTime = serviceTime;
    }
}
