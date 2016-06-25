package com.girigiri.dao;

import com.girigiri.dao.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by JianGuo on 6/25/16.
 */
@Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        this.customerRepository.save(new Customer("420104199601021617", "13018060139", "my address", "my contactName"));
    }
}
