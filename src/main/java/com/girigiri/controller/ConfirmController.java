package com.girigiri.controller;

import com.girigiri.dao.services.CustomerRepository;
import com.girigiri.dao.services.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by JianGuo on 6/26/16.
 * Rest controller for getting a confirmation from {@link com.girigiri.dao.models.Customer}
 */
@RestController
@RequestMapping(value = "/api/confirm")
public class ConfirmController extends BaseController {
    private final CustomerRepository customerRepository;
    private final DeviceRepository deviceRepository;

    @Autowired
    ConfirmController(CustomerRepository customerRepository, DeviceRepository deviceRepository) {
        this.customerRepository = customerRepository;
        this.deviceRepository = deviceRepository;
    }



    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String getAllConfirm() {
        return "all value here";
    }


    // TODO: 6/26/16 regroup customer and device to a confirm POJO
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getConfirm(@PathVariable String id) {
        return id;
    }

}
