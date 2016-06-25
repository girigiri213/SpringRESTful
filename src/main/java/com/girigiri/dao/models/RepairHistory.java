package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * Created by JianGuo on 6/24/16.
 * POJO for repair history in this system
 */
@Data
@Entity
@Table(name = "history")
public class RepairHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String time;

    private String checkHistory;

    private String repairHistory;

    private String repairTime;

    private String workforce;

    private String manPrice;

    private String materialPrice;

    private String promise;

    private String warning;

    //TODO: limit state to 1, 2, 3 or 4
    @Max(4)
    @Min(1)
    private int repairState;

    //TODO: limit state to 1, 2 or 3
    @Max(3)
    @Min(1)
    private int delayType;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    private Date updated;

    @Version
    @JsonIgnore
    private Long version;





    @PrePersist
    protected void onCreate() {
        updated = created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }


    public RepairHistory() {}


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCheckHistory() {
        return checkHistory;
    }

    public void setCheckHistory(String checkHistory) {
        this.checkHistory = checkHistory;
    }

    public String getRepairHistory() {
        return repairHistory;
    }

    public void setRepairHistory(String repairHistory) {
        this.repairHistory = repairHistory;
    }

    public String getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(String repairTime) {
        this.repairTime = repairTime;
    }

    public String getWorkforce() {
        return workforce;
    }

    public void setWorkforce(String workforce) {
        this.workforce = workforce;
    }

    public String getManPrice() {
        return manPrice;
    }

    public void setManPrice(String manPrice) {
        this.manPrice = manPrice;
    }

    public String getMaterialPrice() {
        return materialPrice;
    }

    public void setMaterialPrice(String materialPrice) {
        this.materialPrice = materialPrice;
    }

    public String getPromise() {
        return promise;
    }

    public void setPromise(String promise) {
        this.promise = promise;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public int getRepairState() {
        return repairState;
    }

    public void setRepairState(int repairState) {
        this.repairState = repairState;
    }

    public int getDelayType() {
        return delayType;
    }

    public void setDelayType(int delayType) {
        this.delayType = delayType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepairHistory that = (RepairHistory) o;

        if (repairState != that.repairState) return false;
        if (delayType != that.delayType) return false;
        if (!id.equals(that.id)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (checkHistory != null ? !checkHistory.equals(that.checkHistory) : that.checkHistory != null) return false;
        if (repairHistory != null ? !repairHistory.equals(that.repairHistory) : that.repairHistory != null)
            return false;
        if (repairTime != null ? !repairTime.equals(that.repairTime) : that.repairTime != null) return false;
        if (workforce != null ? !workforce.equals(that.workforce) : that.workforce != null) return false;
        if (manPrice != null ? !manPrice.equals(that.manPrice) : that.manPrice != null) return false;
        if (materialPrice != null ? !materialPrice.equals(that.materialPrice) : that.materialPrice != null)
            return false;
        if (promise != null ? !promise.equals(that.promise) : that.promise != null) return false;
        return warning != null ? warning.equals(that.warning) : that.warning == null;

    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (checkHistory != null ? checkHistory.hashCode() : 0);
        result = 31 * result + (repairHistory != null ? repairHistory.hashCode() : 0);
        result = 31 * result + (repairTime != null ? repairTime.hashCode() : 0);
        result = 31 * result + (workforce != null ? workforce.hashCode() : 0);
        result = 31 * result + (manPrice != null ? manPrice.hashCode() : 0);
        result = 31 * result + (materialPrice != null ? materialPrice.hashCode() : 0);
        result = 31 * result + (promise != null ? promise.hashCode() : 0);
        result = 31 * result + (warning != null ? warning.hashCode() : 0);
        result = 31 * result + repairState;
        result = 31 * result + delayType;
        return result;
    }

    @Override
    public String toString() {
        return "RepairHistory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", checkHistory='" + checkHistory + '\'' +
                ", repairHistory='" + repairHistory + '\'' +
                ", repairTime='" + repairTime + '\'' +
                ", workforce='" + workforce + '\'' +
                ", manPrice='" + manPrice + '\'' +
                ", materialPrice='" + materialPrice + '\'' +
                ", promise='" + promise + '\'' +
                ", warning='" + warning + '\'' +
                ", repairState=" + repairState +
                ", delayType=" + delayType +
                '}';
    }
}
