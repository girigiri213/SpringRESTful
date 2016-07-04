package com.girigiri.dao.services;

import com.girigiri.dao.models.Customer;
import com.girigiri.dao.models.Request;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link Customer}
 */
@RepositoryRestResource(exported = false)
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {


    @Transactional(readOnly = true)
    @Query("FROM Customer L WHERE L.userId LIKE %:userId% AND L.mobile LIKE %:mobile% AND L.contactName LIKE %:contactName%")
    List<Customer> search(@Param("userId") String userId, @Param("mobile") String mobile, @Param("contactName") String contactName);
}
