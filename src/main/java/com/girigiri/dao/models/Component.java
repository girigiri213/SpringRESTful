package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by JianGuo on 6/24/16.
 * POJO for spare parts requested in this system
 */
@Entity
@Data
@Table
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    private String serial;

    @NotNull
    @Min(0)
    private int price;

    @NotNull
    @Min(0)
    private int size;

    @NotNull
    @Min(0)
    private int warningSize;

    private int state;

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

    public Long getUpdated() {
        return updated;
    }

    @Version
    @JsonIgnore
    private Long version;


    private Link _links;

    public Link get_links() {
        return _links;
    }

    public void set_links(Link _links) {
        this._links = _links;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        setState();
    }

    public int getWarningSize() {
        return warningSize;
    }

    public void setWarningSize(int warningSize) {
        this.warningSize = warningSize;
        setState();
    }

    public int getState() {
        return state;
    }

    public void setState() {
        if (size > warningSize) {
            state = 1;
        } else if (size == warningSize) {
            state = 2;
        } else if (size < warningSize && size > 0) {
            state = 3;
        } else {
            state = 4;
        }
    }


    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public Component() {

    }

    public Component(String name, int price, int size, int warningSize) {
        this(name, null, price, size, warningSize);
    }

    public Component(String name, String serial, int price, int size, int warningSize) {
        this.name = name;
        this.serial = serial;
        this.price = price;
        this.size = size;
        this.warningSize = warningSize;
        setState();
    }

}


