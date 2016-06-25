package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
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

    private Long predictTime;

    //TODO: limit the state to 1, 2 or 3
    private int state;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    private Date updated;

    @Version
    @JsonIgnore
    private Long version;

    @PrePersist  //Callback when item creates
    protected void onCreate() {
        updated = created = new Date();
    }

    @PreUpdate   //Callback when item updates
    protected void onUpdate() {
        updated = new Date();
    }


    public Request(Long time, int predictPrice, Long predictTime, int state) {
        this.time = time;
        this.predictPrice = predictPrice;
        this.predictTime = predictTime;
        this.state = state;
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

    public Long getPredictTime() {
        return predictTime;
    }

    public void setPredictTime(Long predictTime) {
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
                ", predictTime=" + predictTime +
                ", state=" + state +
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
        int result = id.hashCode();
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + predictPrice;
        result = 31 * result + (predictTime != null ? predictTime.hashCode() : 0);
        result = 31 * result + state;
        return result;
    }
}
