package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by JianGuo on 6/24/16.
 * POJO for Customer in this system
 */
@Data
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue()
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    private Date updated;

    @Version
    @JsonIgnore
    private Long version;

    @NotNull
    private String userId;

    //TODO: Limit type to 1, 2, 3 or 4
    @Min(1)
    @Max(4)
    private int type;

    private String companyName;

    private String phone;

    @NotNull
    private String mobile;

    @NotNull
    private String address;

    private String zip;

    @NotNull
    private String contactName;


    private String email;


    public Customer(String userId, String mobile, String address, String contactName) {
        this(userId, 1, null, null, mobile, address, null, contactName, null);
    }

    public Customer() {}


    public Customer(Long id, String userId, String mobile, String address, String contactName) {
        this(id, userId, 1, null, null, mobile, address, null, contactName, null);
    }

    public Customer(Long id, String userId, int type, String companyName, String phone, String mobile, String address, String zip, String contactName, String email) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.companyName = companyName;
        this.phone = phone;
        this.mobile = mobile;
        this.address = address;
        this.zip = zip;
        this.contactName = contactName;
        this.email = email;
    }


    public Customer(String userId, int type, String companyName, String phone, String mobile, String address, String zip, String contactName, String email) {
        this.userId = userId;
        this.type = type;
        this.companyName = companyName;
        this.phone = phone;
        this.mobile = mobile;
        this.address = address;
        this.zip = zip;
        this.contactName = contactName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    @PrePersist
    protected void onCreate() {
        updated = created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }



    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", creation time=" + created +
                ", userId=" + userId +
                ", type=" + type +
                ", companyName='" + companyName + '\'' +
                ", phone='" + phone + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                ", zip='" + zip + '\'' +
                ", contactName='" + contactName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
