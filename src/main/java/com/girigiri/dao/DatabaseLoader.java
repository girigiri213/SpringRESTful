package com.girigiri.dao;

import com.girigiri.dao.models.*;
import com.girigiri.dao.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

/**
 * Created by JianGuo on 6/25/16.
 * database loader for init.
 */
//Comment this if you don't want any initial data.
@org.springframework.stereotype.Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RepairHistoryRepository repairHistoryRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ComponentRequestRepository componentRequestRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public void run(String... args) throws Exception {
//        Customer customer = new Customer("420104199601021617", "13018060139", "my address", "my contactName");
//        Device device = new Device(1, "some error", 1);
//        Request request = new Request(155, "2016-7-7", 1);
//        request.setDevice(device);
//
//        Customer rst = customerRepository.save(customer);
//        request.setCusId(rst.getId());
//        RepairHistory repairHistory = new RepairHistory();
//        repairHistory.setDelayType(1);
//        repairHistory.setRepairState(1);
//        request.setRepairHistory(repairHistory);
//        ComponentRequest componentRequest = new ComponentRequest("name", "serial", 100);
//        componentRequest.setState(1);
//        componentRequest.setHistory(1);
//        componentRequestRepository.save(componentRequest);
//        componentRequest = new ComponentRequest("other name", "other serial", 200);
//        componentRequest.setHistory(2);
//        componentRequest.setState(2);
//        componentRequestRepository.save(componentRequest);
//        requestRepository.save(request);
//
//        Customer customer1 = new Customer("420104199601021617", "13018060139", "my new address", "my new contactName");
//        Request request2 = new Request(450, "2016-4-5", 2);
//        Device device2 = new Device(1, "some other error", 2);
//        request2.setDevice(device2);
//        customerRepository.save(customer1);
//        requestRepository.save(request2);
//        Request request1 = new Request(200, "2014-13-2", 2);
//        Device device1 = new Device(1, "some new error", 2);
//        request1.setDevice(device1);
//        requestRepository.save(request1);
        Manager manager = new Manager("guojian", "password", Manager.ROLE_USER);

        managerRepository.save(manager);
        managerRepository.save(new Manager("ladrift", "password", Manager.ROLE_USER));
    }
}
