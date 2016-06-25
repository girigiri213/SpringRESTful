package com.girigiri.dao;

import com.girigiri.dao.models.ComponentRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for {@link com.girigiri.dao.models.ComponentRequest}
 */
public interface ComponentRequestRepository extends PagingAndSortingRepository<ComponentRequest, Long> {
}
