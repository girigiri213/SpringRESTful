package com.girigiri.dao.services;

import com.girigiri.dao.models.Device;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.config.EnableHypermediaSupport;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link Device}
 */
//@RepositoryRestResource(exported = false)
public interface DeviceRepository extends PagingAndSortingRepository<Device, Long> {
}
