package com.girigiri.dao.models;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by JianGuo on 6/21/16.
 * Demo model for JDBC Access
 */
@Data
@Entity
@Table(name = "employee")
public class Employee {
    private @Id
    @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private String firstName;
    private String lastName;
    private String description;

    private Employee() {}

    public Employee(String firstName, String lastName, String description) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
