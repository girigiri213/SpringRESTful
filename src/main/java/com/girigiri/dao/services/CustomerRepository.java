package com.girigiri.dao.services;

import com.girigiri.dao.models.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link Customer}
 */
//@RepositoryRestResource(exported = false)
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
}
