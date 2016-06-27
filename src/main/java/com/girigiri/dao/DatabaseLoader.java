package com.girigiri.dao;

import com.girigiri.dao.models.*;
import com.girigiri.dao.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
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
        Customer customer1 = new Customer("420104199601021618", "13018060139", "my other address1", "my contactName");
        Device device = new Device(1, "some error", 1);
        Device device1 = new Device(2, "some other", 2);
        Request request = new Request(150, "2016-4-12", 1, customer, device);
        RepairHistory repairHistory = new RepairHistory(1, 1, request);
        ComponentRequest componentRequest = new ComponentRequest("component","serial1", 1, repairHistory);
        Set<ComponentRequest> componentRequests = new HashSet<>();
        componentRequests.add(componentRequest);
        componentRequest = new ComponentRequest("new component", "serial2", 3, repairHistory);
        componentRequests.add(componentRequest);
        componentRequests.add(new ComponentRequest("new component2", "serial3", 4, repairHistory));
        repairHistory.setComponentRequests(componentRequests);
        requestRepository.save(request);
        this.repairHistoryRepository.save(repairHistory);
//        this.customerRepository.save(new Customer("420104199601021617", "13018060139", "my address", "my contactName"));
//        this.customerRepository.save(new Customer("420104199601021618", "13018060139", "my other address1", "my contactName"));
//        this.customerRepository.save(new Customer("420104199601021619", "13018060139", "my other address2", "my contactName"));
//        this.customerRepository.save(new Customer("420104199601021620", "13018060139", "my other address3", "my contactName"));
    }
}
