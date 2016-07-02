package com.girigiri.controller;

import com.girigiri.controller.utils.Auth;
import com.girigiri.dao.models.Manager;
import com.girigiri.dao.services.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by JianGuo on 7/1/16.
 */
@RestController
@RequestMapping(value = "/api/login")
public class LoginController {

    private final ManagerRepository managerRepository;

    @Autowired
    public LoginController(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> auth(@RequestBody Auth auth) {
        Manager query = managerRepository.findOneByName(auth.getName());
        String pwd = query.getPassword();
        String rawPwd = auth.getPassword();
        if(Manager.PASSWORD_ENCODER.matches(rawPwd, pwd)) {
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }


}
