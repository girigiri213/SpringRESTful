package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by JianGuo on 6/24/16.
 * POJO for component history needed for one request.
 */
@Data
@Entity
@Table
public class ComponentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    private String serial;

    @NotNull
    @Min(0)
    private int size;

    @Version
    @JsonIgnore
    private Long version;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public ComponentRequest() {}

    public ComponentRequest(String name, String serial, int size) {
        this.name = name;
        this.serial = serial;
        this.size = size;
    }

    public ComponentRequest(String name, int size) {
        this.name = name;
        this.size = size;
    }


    @Override
    public String toString() {
        return "ComponentRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serial='" + serial + '\'' +
                ", size=" + size +
                ", version=" + version +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentRequest that = (ComponentRequest) o;

        if (size != that.size) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (serial != null ? !serial.equals(that.serial) : that.serial != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        return updated != null ? updated.equals(that.updated) : that.updated == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (serial != null ? serial.hashCode() : 0);
        result = 31 * result + size;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }
}
