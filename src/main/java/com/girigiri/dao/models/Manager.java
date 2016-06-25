package com.girigiri.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;

/**
 * Created by JianGuo on 6/25/16.
 * manager for this system, this will be implemented with RBAC access.
 * Currently we have several roles in this system:
 * <h1>ROLE_USER</h1> who has access to {@link Customer}, {@link Device}, {@link Request}, {@link RepairHistory}
 * , he has responsibility to CRUD customer , CRUD device to be repaired, submit a repairing request, and set a record for
 * repairing after client confirming it.
 * <h1>ROLE_ENGINEER</h1> who has access to {@link Device}, {@link RepairHistory}, {@link Component}
 * , he has responsibility to read, update device to be repaired, update a repairing record
 * <h1>ROLE_SCHEDULER</h1> who has access to {@link Request}
 * , he has responsibility to dispatch request to different engineer
 * <h1>ROLE_ACCOUNTANT</h1> who has access to {@link RepairHistory}, {@link Customer}
 * , he has responsibility to make bill
 */
@Data
@Entity
@Table(name = "manager")
public class Manager {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ENGINEER = "ROLE_ENGINEER";
    public static final String ROLE_SCHEDULER = "ROLE_SCHEDULER";
    public static final String ROLE_ACCOUNTANT = "ROLE_ACCOUNTANT";
    private @Id
    @GeneratedValue
    Long id;

    private String name;

    private @JsonIgnore
    String password;

    private String[] roles;

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    protected Manager() {}

    public Manager(String name, String password, String... roles) {

        this.name = name;
        this.setPassword(password);
        this.roles = roles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String[] getRoles() {
        return roles;
    }

    public Long getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Manager{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", roles=" + Arrays.toString(roles) +
                '}';
    }
}
