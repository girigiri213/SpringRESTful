package com.girigiri.dao.services;

import com.girigiri.dao.models.Component;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by JianGuo on 6/26/16.
 * DAO service for model {@link Component}
 */
@PreAuthorize("hasRole('REPO_MANAGER')")
public interface ComponentRepository extends PagingAndSortingRepository<Component, Long> {
}
