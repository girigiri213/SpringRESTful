package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.NumberFormat;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    private Date created;
    private Date updated;

    @PrePersist
    protected void onCreate() {
        created = updated = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }

    public Long getCreated() {
        return created.getTime();
    }

    public void setCreated(Date created) {
        if (created == null) {
            return;
        }
        this.created = created;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Long getUpdated() {
        return updated.getTime();
    }

    @Version
    @JsonIgnore
    private Long version;

    @NotNull
    @Size(min = 18, max = 20)
    private String userId;

    @Min(1)
    @Max(4)
    private int type;

    private String companyName;

    @Size(min = 8, max = 12)
    private String phone;

    @NotNull
    @Size(min = 11, max = 11)
    private String mobile;

    @NotNull
    private String address;

    private String zip;

    @NotNull
    private String contactName;

    @Email
    @Nullable
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



}
