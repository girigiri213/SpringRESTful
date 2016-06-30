package com.girigiri.dao.services;

import com.girigiri.dao.models.Request;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model model {@link Request}
 */
public interface RequestRepository extends PagingAndSortingRepository<Request, Long>{

    @RestResource(exported = false)
    @Transactional
    @Modifying
    List<Request> removeByCusId(long cus_id);


}
