package com.girigiri.dao;

import com.girigiri.dao.models.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by JianGuo on 6/21/16.
 * Example DAO for {@link Employee}
 */
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
}
