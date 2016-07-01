package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.girigiri.dao.constraints.StringDateFormat;
import com.girigiri.dao.services.ComponentRepository;
import lombok.Data;
import org.springframework.hateoas.Link;

import javax.ejb.EJB;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @StringDateFormat
    private String assignTime;

    private String checkHistory;

    private String repairHistory;

    @StringDateFormat
    private String repairTime;

    private String workforce;

    private int manPrice;

    private int materialPrice;

    private String promise;

    private String warning;

    @Max(4)
    @Min(1)
    private int repairState;

    @Max(3)
    @Min(1)
    private int delayType;


    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ComponentRequest> componentRequests = new ArrayList<>();

    @Version
    @JsonIgnore
    private Long version;

    private Date created;
    private Date updated;

    private Long managerId;


    @Transient
    private Link _links;

    public Link get_links() {
        return _links;
    }

    public void set_links(Link _links) {
        this._links = _links;
    }

    public RepairHistory() {
        this(1, 1);
    }

    public RepairHistory(int repairState, int delayType) {
        this.repairState = repairState;
        this.delayType = delayType;
    }





    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(String assignTime) {
        this.assignTime = assignTime;
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

    public int getManPrice() {
        return manPrice;
    }

    public void setManPrice(int manPrice) {
        this.manPrice = manPrice;
    }

    public int getMaterialPrice() {
        return materialPrice;
    }

    public void setMaterialPrice(int materialPrice) {
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


    @PrePersist
    protected void onCreate() {
        created = updated = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }


    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
        this.repairState = 2;
    }

    public List<ComponentRequest> getComponentRequests() {
        return componentRequests;
    }

    public void setComponentRequests(List<ComponentRequest> componentRequests) {
        this.componentRequests = componentRequests;
    }

    public void addComponentRequest(ComponentRequest componentRequest) {
        if (componentRequests.contains(componentRequest)) return;
        componentRequests.add(componentRequest);
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
        if (assignTime != null ? !assignTime.equals(that.assignTime) : that.assignTime != null) return false;
        if (checkHistory != null ? !checkHistory.equals(that.checkHistory) : that.checkHistory != null) return false;
        if (repairHistory != null ? !repairHistory.equals(that.repairHistory) : that.repairHistory != null)
            return false;
        if (repairTime != null ? !repairTime.equals(that.repairTime) : that.repairTime != null) return false;
        if (workforce != null ? !workforce.equals(that.workforce) : that.workforce != null) return false;
        if (promise != null ? !promise.equals(that.promise) : that.promise != null) return false;
        return warning != null ? warning.equals(that.warning) : that.warning == null;

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
    public int hashCode() {
        int result = 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (assignTime != null ? assignTime.hashCode() : 0);
        result = 31 * result + (checkHistory != null ? checkHistory.hashCode() : 0);
        result = 31 * result + (repairHistory != null ? repairHistory.hashCode() : 0);
        result = 31 * result + (repairTime != null ? repairTime.hashCode() : 0);
        result = 31 * result + (workforce != null ? workforce.hashCode() : 0);
        result = 31 * result + manPrice;
        result = 31 * result + materialPrice;
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
                ", time='" + assignTime + '\'' +
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
