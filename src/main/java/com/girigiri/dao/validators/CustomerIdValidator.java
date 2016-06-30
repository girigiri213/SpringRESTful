package com.girigiri.dao.validators;

import com.girigiri.dao.models.Customer;
import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * Created by JianGuo on 6/30/16.
 * Validator for valid customer ID.
 */
// FIXME: 6/30/16 NullPointerException in customerRepository
@Component
public class CustomerIdValidator implements org.springframework.validation.Validator {

    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof Request) {
            Request rst = (Request)target;
            if (!customerRepository.exists(rst.getCusId())) {
                errors.rejectValue("customerId", "invalid customer Id");
            }
        }
    }
}
