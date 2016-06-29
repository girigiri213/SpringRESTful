package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

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

    private Long created;
    private Long updated;

    @PrePersist
    protected void onCreate() {
        created = updated = new Date().getTime();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date().getTime();
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        if (created == null) {
            return;
        }
        this.created = created;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Long getUpdated() {
        return updated;
    }

//    @Version()
//    @JsonIgnore
    //TODO: version will change when detaches from old reference
//    private Long version;

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

    @Size(min = 6, max = 6)
    private String zip;

    @NotNull
    private String contactName;

    @Email
    @Nullable
    private String email;

//    //// FIXME: 6/28/16 Error when delete a customer
//    @OneToMany(mappedBy = "customer")
//    private List<Request> requests = new ArrayList<>();
//
//    public void setRequests(List<Request> requests) {
//        this.requests = requests;
//    }
//
//    public List<Request> getRequests() {
//        return Collections.unmodifiableList(requests);
//    }
//
//
//    public void addRequest(Request request) {
//        if (requests.contains(request)) return;
//        if (request.getCustomer() != null) {
//            request.getCustomer().removeRequest(request);
//        }
//        requests.add(request);
//        request.setCustomer(this);
//    }
//
//    public void removeRequest(Request request) {
//        if (!requests.contains(request)) return;
//        requests.remove(request);
//        request.setCustomer(null);
//    }




    public Customer() {}


    public Customer(String userId, String mobile, String address, String contactName) {
        this.userId = userId;
        this.mobile = mobile;
        this.address = address;
        this.contactName = contactName;
        this.type = 1;
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



    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", created=" + created +
                ", updated=" + updated +
//                ", version=" + version +
                ", userId='" + userId + '\'' +
                ", type=" + type +
                ", companyName='" + companyName + '\'' +
                ", phone='" + phone + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                ", zip='" + zip + '\'' +
                ", contactName='" + contactName + '\'' +
                ", email='" + email + '\'' +
//                ", requests=" + requests +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (type != customer.type) return false;
        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;
        if (created != null ? !created.equals(customer.created) : customer.created != null) return false;
        if (updated != null ? !updated.equals(customer.updated) : customer.updated != null) return false;
//        if (version != null ? !version.equals(customer.version) : customer.version != null) return false;
        if (userId != null ? !userId.equals(customer.userId) : customer.userId != null) return false;
        if (companyName != null ? !companyName.equals(customer.companyName) : customer.companyName != null)
            return false;
        if (phone != null ? !phone.equals(customer.phone) : customer.phone != null) return false;
        if (mobile != null ? !mobile.equals(customer.mobile) : customer.mobile != null) return false;
        if (address != null ? !address.equals(customer.address) : customer.address != null) return false;
        if (zip != null ? !zip.equals(customer.zip) : customer.zip != null) return false;
        if (contactName != null ? !contactName.equals(customer.contactName) : customer.contactName != null)
            return false;
        return email != null ? email.equals(customer.email) : customer.email == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
//        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (contactName != null ? contactName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    public void setId(long id) {
        this.id = id;
    }
}
