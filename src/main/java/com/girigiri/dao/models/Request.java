package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.girigiri.dao.constraints.StringDateFormat;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

    @JsonIgnore
    private Long time;

    private int predictPrice;

    @StringDateFormat
    private String predictTime;

    @Min(1)
    @Max(3)
    private int state;

    private Long created;

    private Long updated;

    @Transient
    private Link _links;

    public Link get_links() {
        return _links;
    }

    public void set_links(Link _links) {
        this._links = _links;
    }

    public Long getCreated() {
        return created;
    }

//    public void setCreated(Date created) {
//        if (created == null) {
//            return;
//        }
//        this.created = created;
//    }
//
//    public void setUpdated(Date updated) {
//        this.updated = updated;
//    }

    public Long getUpdated() {
        return updated;
    }




    @Version
    @JsonIgnore
    private Long version;



    private Long cusId;

    public Long getCusId() {
        return cusId;
    }

    public void setCusId(Long cusId) {
        this.cusId = cusId;
    }

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Device device;


    public Device getDevice() {
        return device;
    }

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    public void setDevice(Device newDevice) {
        if (sameAsFormer(newDevice)) return;
        this.device = newDevice;
    }

    private boolean sameAsFormer(Device newDevice) {
        return device == null ?
                newDevice == null : device.equals(newDevice);
    }

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private RepairHistory repairHistory;

    public RepairHistory getRepairHistory() {
        return repairHistory;
    }
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    public void setRepairHistory(RepairHistory repairHistory) {
        if (this.repairHistory == null ? repairHistory == null: this.repairHistory.equals(repairHistory)) return;
        this.repairHistory = repairHistory;
    }


    @PrePersist
    protected void onCreate() {

        created = updated = new Date().getTime();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date().getTime();
    }

    public Request() {

    }


    public Request(int predictPrice, String predictTime, int state) {
        this.predictPrice =predictPrice;
        this.predictTime = predictTime;
        this.state = state;
        this.time = new Date().getTime();
    }

    public Request(Long time, int predictPrice, String predictTime, int state) {
        this.time = time;
        this.predictPrice = predictPrice;
        this.predictTime = predictTime;
        this.state = state;
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
                ", cusId=" + cusId +
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
