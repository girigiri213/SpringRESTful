package com.girigiri.controller;

import com.girigiri.dao.models.Manager;
import com.girigiri.dao.services.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JianGuo on 7/4/16.
 * Controller for {@link Manager}
 */
@RestController
@RequestMapping(value = "/api/manager")
public class ManagerController {
    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerController(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @RequestMapping(value = "/engineer", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAllEngineer() {
        Iterable<Manager> iterable = managerRepository.findAll();
        List<Manager> managers = new ArrayList<>();
        iterable.forEach(manager -> {
            if (containsCorrectRole(manager.getRoles())) {
                managers.add(manager);
            }
        });
        return ResponseEntity.ok(managers);
    }

    private boolean containsCorrectRole(String[] roles) {
        for (String role : roles) {
            if (role.equals(Manager.ROLE_ENGINEER)) {
                return true;
            }
        }
        return false;
    }
}
