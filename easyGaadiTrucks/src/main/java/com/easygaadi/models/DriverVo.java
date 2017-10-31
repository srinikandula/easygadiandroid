package com.easygaadi.models;

/**
 * Created by ssv on 30-10-2017.
 */

public class DriverVo {
    private String accountId;

    private String truckId;
    private String driverId;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    private String __v;

    private String updatedBy;

    private String joiningDate;

    private String updatedAt;

    private String _id;

    private String createdBy;

    private String createdAt;

    private String fullName;

    private String salary;

    private String licenseValidity;
    private String licenseNumber;

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    private String mobile;

    public String getAccountId ()
    {
        return accountId;
    }

    public void setAccountId (String accountId)
    {
        this.accountId = accountId;
    }

    public String getTruckId ()
    {
        return truckId;
    }

    public void setTruckId (String truckId)
    {
        this.truckId = truckId;
    }

    public String get__v ()
    {
        return __v;
    }

    public void set__v (String __v)
    {
        this.__v = __v;
    }

    public String getUpdatedBy ()
    {
        return updatedBy;
    }

    public void setUpdatedBy (String updatedBy)
    {
        this.updatedBy = updatedBy;
    }

    public String getJoiningDate ()
    {
        return joiningDate;
    }

    public void setJoiningDate (String joiningDate)
    {
        this.joiningDate = joiningDate;
    }

    public String getUpdatedAt ()
    {
        return updatedAt;
    }

    public void setUpdatedAt (String updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getCreatedBy ()
    {
        return createdBy;
    }

    public void setCreatedBy (String createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getCreatedAt ()
    {
        return createdAt;
    }

    public void setCreatedAt (String createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getFullName ()
    {
        return fullName;
    }

    public void setFullName (String fullName)
    {
        this.fullName = fullName;
    }


    public String getLicenseValidity ()
    {
        return licenseValidity;
    }

    public void setLicenseValidity (String licenseValidity)
    {
        this.licenseValidity = licenseValidity;
    }

    public String getMobile ()
    {
        return mobile;
    }

    public void setMobile (String mobile)
    {
        this.mobile = mobile;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [accountId = "+accountId+", truckId = "+truckId+", __v = "+__v+", updatedBy = "+updatedBy+", joiningDate = "+joiningDate+", updatedAt = "+updatedAt+", _id = "+_id+", createdBy = "+createdBy+", createdAt = "+createdAt+", fullName = "+fullName+", salary = "+salary+", licenseValidity = "+licenseValidity+", mobile = "+mobile+"]";
    }
}
