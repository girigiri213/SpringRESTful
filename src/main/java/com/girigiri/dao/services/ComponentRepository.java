package com.girigiri.dao.services;

import com.girigiri.dao.models.Component;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by JianGuo on 6/26/16.
 * DAO service for model {@link Component}
 */
@RepositoryRestResource(path = "/components")
public interface ComponentRepository extends PagingAndSortingRepository<Component, Long> {

    @Transactional
    @RestResource(path = "/findByName", rel = "findByName")
    @Query("FROM Component L WHERE L.name LIKE %:name%")
    List<Component> findByName(@Param("name") String name);

}
