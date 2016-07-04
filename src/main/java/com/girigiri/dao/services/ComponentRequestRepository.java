package com.girigiri.dao.services;

import com.girigiri.dao.models.ComponentRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for {@link com.girigiri.dao.models.ComponentRequest}
 */
@RepositoryRestResource(exported = false)
public interface ComponentRequestRepository extends PagingAndSortingRepository<ComponentRequest, Long> {
    List<ComponentRequest> findByHistory(Long history);

    @Transactional
    @Query("FROM ComponentRequest L WHERE NAME LIKE %:name%")
    List<ComponentRequest> findByName(@Param("name") String name);
}
