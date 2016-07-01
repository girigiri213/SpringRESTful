package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.Max;
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

    private Long created;
    private Long updated;

    @Max(3)
    @Min(1)
    private int state;

    private int price;


    private long history;

    @Transient
    private Link _link;

    public Link get_link() {
        return _link;
    }

    public void set_link(Link _link) {
        this._link = _link;
    }

    public long getHistory() {
        return history;
    }

    public void setHistory(long history) {
        this.history = history;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

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


    public Long getUpdated() {
        return updated;
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






    public ComponentRequest() {
        this.state = 1;
    }


    public ComponentRequest(String name, String serial, int size) {
        this.name = name;
        this.serial = serial;
        this.size = size;
        this.state = 1;
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
