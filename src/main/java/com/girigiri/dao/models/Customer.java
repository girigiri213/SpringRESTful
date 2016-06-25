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
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private Long userId;

    //TODO: Limit type to 1, 2, 3 or 4
    @Max(4)
    @Min(1)
    private int type;

    private String companyName;

    private String phone;

    @NotNull
    private String mobile;

    @NotNull
    private String address;

    private String zip;

    private String contactName;

    @NotNull
    private String email;


    public Customer(Long userId, String mobile, String address, String email) {
        this(userId, 1, null, null, mobile, address, null, null, email);
    }

    public Customer() {}

    public Customer(Long userId, int type, String companyName, String phone, String mobile, String address, String zip, String contactName, String email) {
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


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (type != customer.type) return false;
        if (!id.equals(customer.id)) return false;
        if (!created.equals(customer.created)) return false;
        if (!userId.equals(customer.userId)) return false;
        if (companyName != null ? !companyName.equals(customer.companyName) : customer.companyName != null)
            return false;
        if (phone != null ? !phone.equals(customer.phone) : customer.phone != null) return false;
        if (!mobile.equals(customer.mobile)) return false;
        if (!address.equals(customer.address)) return false;
        if (zip != null ? !zip.equals(customer.zip) : customer.zip != null) return false;
        if (!contactName.equals(customer.contactName)) return false;
        return email != null ? email.equals(customer.email) : customer.email == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + created.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + type;
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + mobile.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + contactName.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
