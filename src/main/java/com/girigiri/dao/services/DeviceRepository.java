package com.girigiri.dao.services;

import com.girigiri.dao.models.Device;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.config.EnableHypermediaSupport;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link Device}
 */
// It will not be exported for it is merged into Request
@RepositoryRestResource(exported = false)
public interface DeviceRepository extends PagingAndSortingRepository<Device, Long> {
    @Override
    @RestResource(exported = false)
    void delete(Long aLong);


    @Override
    @RestResource(exported = false)
    void delete(Iterable<? extends Device> entities);


    @Override
    @RestResource(exported = false)
    void delete(Device entity);


}
