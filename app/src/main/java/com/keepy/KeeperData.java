package com.keepy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/*
 * This class is used to store the data of a keeper
 * It is used to store the keeper's fees and rating
 * We use this class to store the data in the database and
 * in a way that is easy to use in the app
 */
public class KeeperData {
    private HashMap<String, Integer> fees;

    private int ratingCount;
    private float rating;

    private List<ServiceTime> serviceTimes = Constants.Utils.getDefaultAllDayServiceTimes();

    public KeeperData(HashMap<String,Integer> fees, int rating, int ratingCount) {
        this.fees = fees;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public List<ServiceTime> getServiceTimes() {
        return serviceTimes;
    }

    public void setServiceTimes(List<ServiceTime> serviceTimes) {
        this.serviceTimes = serviceTimes;
    }


    public boolean servesTime(int startHour,int startMinute) {
        for (ServiceTime serviceTime : serviceTimes) {
            if (startHour >= serviceTime.getStartHour() && startHour <= serviceTime.getEndHour()) {
                if (startHour == serviceTime.getStartHour() && startMinute <  serviceTime.getStartMinute()) {
                    continue;
                }
                if (startHour == serviceTime.getEndHour() && startMinute > serviceTime.getEndMinute()) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    public float getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public KeeperData() { /* empty constructor for firebase */ }

    public HashMap<String, Integer> getFees() {
        return new HashMap<>(fees);
    } /* return a copy of the fees */

    public void setFees(HashMap<String, Integer> fees) {
        this.fees = fees;
    }
}
