package com.girigiri.dao;

import com.girigiri.dao.models.*;
import com.girigiri.dao.services.*;
import com.sun.org.apache.regexp.internal.REUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by JianGuo on 6/25/16.
 * database loader for init.
 */
//Comment this if you don't want any initial data.
@Component
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

    @Override
    public void run(String... args) throws Exception {
        Customer customer = new Customer("420104199601021617", "13018060139", "my address", "my contactName");
        Device device = new Device(1, "some error", 1);
        Request request = new Request(155, "2016-7-7", 1);
        request.setDevice(device);

        Customer rst = customerRepository.save(customer);
        request.setCusId(rst.getId());
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setDelayType(1);
        repairHistory.setRepairState(1);
        request.setRepairHistory(repairHistory);
        List<ComponentRequest> list = new ArrayList<>();
        ComponentRequest componentRequest = new ComponentRequest("name", "serial", 100);
        list.add(componentRequest);
        componentRequest = new ComponentRequest("other name", "other serial", 200);
        list.add(componentRequest);
        repairHistory.setComponentRequests(list);
        requestRepository.save(request);

//        Customer customer1 = new Customer("420104199601021617", "13018060139", "my new address", "my new contactName");
//        Request request2 = new Request(450, "2016-4-5", 2);
//        Device device2 = new Device(1, "some other error", 2);
//        request2.setDevice(device2);
//        request2.setCustomer(customer1);
//        customerRepository.save(customer1);
//        requestRepository.save(request2);
//        Request request1 = new Request(200, "2014-13-2", 2);
//        request1.setCustomer(customer);
//        Device device1 = new Device(1, "some new error", 2);
//        request1.setDevice(device1);
//        requestRepository.save(request1);

    }
}
