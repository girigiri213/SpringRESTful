package com.girigiri.dao.services;

import com.girigiri.dao.models.ComponentRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
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
    List<ComponentRequest> findByName(String name);
}
