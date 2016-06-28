package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.girigiri.dao.constraints.StringDateFormat;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by JianGuo on 6/24/16.
 * POJO for to-do repair request history
 */
@Data
@Entity
@Table(name = "request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long time;

    private int predictPrice;

    @StringDateFormat
    private String predictTime;

    @Min(1)
    @Max(3)
    @NotNull
    private int state;

    private Date created;
    private Date updated;

    @Version
    @JsonIgnore
    private Long version;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
//    @JoinColumn(name = "CUS_ID", nullable = false)
    private Customer customer;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private Device device;

    @PrePersist
    protected void onCreate() {
        created = updated = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }

    public Request() {

    }

    public Request(int predictPrice, String predictTime, int state, Customer customer, Device device) {
        this(new Date().getTime(), predictPrice, predictTime, state, customer, device);
    }

    public Request(Long time, int predictPrice, String predictTime, int state, Customer customer, Device device) {
        this.time = time;
        this.predictPrice = predictPrice;
        this.predictTime = predictTime;
        this.state = state;
        this.customer = customer;
        this.device = device;
    }


    public Long getId() {
        return id;
    }

    public int getPredictPrice() {
        return predictPrice;
    }

    public void setPredictPrice(int predictPrice) {
        this.predictPrice = predictPrice;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getPrice() {
        return predictPrice;
    }

    public void setPrice(int predictPrice) {
        this.predictPrice = predictPrice;
    }

    public String getPredictTime() {
        return predictTime;
    }

    public void setPredictTime(String predictTime) {
        this.predictTime = predictTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

//    @JsonBackReference(value = "customer")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer( Customer customer) {
        this.customer = customer;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
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


    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", time=" + time +
                ", predictPrice=" + predictPrice +
                ", predictTime='" + predictTime + '\'' +
                ", state=" + state +
                ", created=" + created +
                ", updated=" + updated +
                ", version=" + version +
                ", customer=" + customer +
                ", device=" + device +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request that = (Request) o;

        if (predictPrice != that.predictPrice) return false;
        if (state != that.state) return false;
        if (!id.equals(that.id)) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        return predictTime != null ? predictTime.equals(that.predictTime) : that.predictTime == null;

    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + predictPrice;
        result = 31 * result + (predictTime != null ? predictTime.hashCode() : 0);
        result = 31 * result + state;
        return result;
    }
}
