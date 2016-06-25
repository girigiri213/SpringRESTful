package com.girigiri.dao;

import com.girigiri.dao.models.RepairHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model {@link RepairHistory}
 */
public interface RepairHistoryRepository extends PagingAndSortingRepository<RepairHistory, Long>{
}
