package com.easygaadi.models;

/**
 * Created by ssv i3-210 on 11/18/2017.
 */

public class PendingPaymentVo {

    String  _id;
    String  date;
    String  freightAmount;
    String  Amount;
    String  tripId;
    String  truckName;
    String  partyName;

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(String freightAmount) {
        this.freightAmount = freightAmount;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
}
