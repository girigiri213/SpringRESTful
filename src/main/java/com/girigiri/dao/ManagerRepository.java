package com.girigiri.dao;

import com.girigiri.dao.models.Manager;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link Manager},
 */
//TODO: should this be exported to client?
//@RepositoryRestResource(exported = false)
public interface ManagerRepository extends CrudRepository<Manager, Long> {
}
