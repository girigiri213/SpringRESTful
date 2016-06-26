package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
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

    private String type;

    @NotNull
    private int size;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    @Version
    @JsonIgnore
    private Long version;



    public Long getId() {
        return id;
    }
}
