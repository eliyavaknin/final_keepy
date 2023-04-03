package com.keepy.models;


import com.keepy.KeeperData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class User implements Serializable {
    private String mEmail;

    private KeeperData mKeeperData;

    private List<String> clientPrefs;
    private List<String> ratedKeepers;
    private Boolean mIsClient;
    private Boolean mIsKeeper;
    private String mUserID;
    private String mFullName;
    private String mPhone;
    private String mLocation;
    private String mAboutMe;



    public User() {
        this("", false, false);
    }


    public User(String email, Boolean isClient, Boolean isKeeper) {
        this(email, isClient, isKeeper, "", "", "", "", null, new ArrayList<>(), new ArrayList<>());

    }

    public User(String email, Boolean isClient, Boolean isKeeper, String fullName, String location, String phone, String aboutMe, KeeperData keeperData, List<String> clientPrefs,
                List<String> ratedKeepers) {
        mEmail = email;
        this.clientPrefs = clientPrefs;
        mIsClient = isClient;
        mIsKeeper = isKeeper;
        mFullName = fullName;
        mKeeperData = keeperData;
        mPhone = phone;
        mLocation = location;
        mAboutMe = aboutMe;
        this.ratedKeepers = ratedKeepers;
    }

    public List<String> getClientPrefs() {
        return clientPrefs;
    }

    public void setClientPrefs(List<String> clientPrefs) {
        this.clientPrefs = clientPrefs;
    }

    public String getmEmail() {
        return this.mEmail;
    }

    public Boolean getmIsClient() {
        return this.mIsClient;

    }

    public String getmUserID() {
        return this.mUserID;
    }

    public String getmPhone() {
        return this.mPhone;
    }

    public String getmFullName() {
        return this.mFullName;
    }

    public String getmLocation() {
        return this.mLocation;
    }

    public String getmAboutMe() {
        return this.mAboutMe;
    }

    public Boolean getmIsKeeper() {
        return this.mIsKeeper;
    }

    public void setRatedKeepers(List<String> ratedKeepers) {
        this.ratedKeepers = ratedKeepers;
    }

    public List<String> getRatedKeepers() {
        return ratedKeepers;
    }

    public void setmIsKeeper(Boolean keeper) {
        this.mIsKeeper = keeper;
    }

    public void setmIsClient(Boolean client) {
        this.mIsClient = client;
    }

    public void setmEmail(String email) {
        this.mEmail = email;
    }

    public void setmUserID(String UserID) {
        this.mUserID = UserID;
    }

    public void setmPhone(String phone) {
        this.mPhone = phone;
    }

    public void setmFullName(String fullName) {
        this.mFullName = fullName;
    }

    public void setmLocation(String location) {
        this.mLocation = location;
    }

    public void setmAboutMe(String aboutMe) {
        this.mAboutMe = aboutMe;
    }


    public void setmKeeperData(KeeperData mKeeperData) {
        this.mKeeperData = mKeeperData;
    }

    public KeeperData getmKeeperData() {
        return mKeeperData;
    }

}
