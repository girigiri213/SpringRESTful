package com.girigiri.dao.services;

import com.girigiri.dao.models.Manager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link Manager},
 */
@RepositoryRestResource(exported = false)
public interface ManagerRepository extends CrudRepository<Manager, Long> {
}
