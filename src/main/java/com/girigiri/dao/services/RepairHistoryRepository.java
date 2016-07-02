package com.girigiri.dao.services;

import com.girigiri.dao.models.RepairHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link RepairHistory}
 */
@RepositoryRestResource(path = "/histories")
public interface RepairHistoryRepository extends PagingAndSortingRepository<RepairHistory, Long>{

    @Transactional(readOnly = true)
    @RestResource(path = "/findByState", rel = "findByState")
    @Query("FROM RepairHistory L WHERE L.repairState = :state")
    List<RepairHistory> findByRepairState(@Param("state") int repairState);

}
