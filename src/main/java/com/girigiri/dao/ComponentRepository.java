package com.girigiri.dao;

import com.girigiri.dao.models.Component;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by JianGuo on 6/26/16.
 * DAO service for model {@link Component}
 */
public interface ComponentRepository extends PagingAndSortingRepository<Component, Long> {
}
