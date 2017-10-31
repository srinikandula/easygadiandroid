package com.easygaadi.models;

import java.io.Serializable;

/**
 * Created by ibraincpu6 on 05-12-2016.
 */

public class GroupItem implements Serializable {
    public String groupname;
    public String id;
    public String contactName;
    public String contactPhone;
    public String password;


    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GroupItem(String groupname, String id, String contactName, String contactPhone, String password) {
        this.groupname = groupname;
        this.id = id;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.password = password;

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}
