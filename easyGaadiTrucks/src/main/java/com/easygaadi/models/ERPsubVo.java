package com.easygaadi.models;

/**
 * Created by ssv i3-210 on 11/20/2017.
 */

public class ERPsubVo {

    String  date;
    String  tripid;
    String  partyname;
    String  freight;
    String  expense;
    String  mis;

    public String getMis() {
        return mis;
    }

    public void setMis(String mis) {
        this.mis = mis;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getPartyname() {
        return partyname;
    }

    public void setPartyname(String partyname) {
        this.partyname = partyname;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }
}
