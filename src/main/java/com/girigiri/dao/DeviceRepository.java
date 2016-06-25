package com.girigiri.dao;

import com.girigiri.dao.models.Device;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link Device}
 */
public interface DeviceRepository extends PagingAndSortingRepository<Device, Long> {
}
