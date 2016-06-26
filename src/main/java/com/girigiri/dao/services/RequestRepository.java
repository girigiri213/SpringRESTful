package com.girigiri.dao.services;

import com.girigiri.dao.models.Request;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model model {@link Request}
 */
public interface RequestRepository extends PagingAndSortingRepository<Request, Long>{
}
