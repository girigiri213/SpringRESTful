package com.girigiri.dao.services;

import com.girigiri.dao.models.ComponentHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by JianGuo on 7/1/16.
 * DAO service for model {@link ComponentHistory}
 */
@RepositoryRestResource(path = "/api/his_component")
public interface ComponentHistoryRepository extends CrudRepository<ComponentHistory, Long> {
}
