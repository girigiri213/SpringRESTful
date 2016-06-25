package com.girigiri.dao;

import com.girigiri.dao.models.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link Customer}
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
}
