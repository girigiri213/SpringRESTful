package com.girigiri.dao.services;

import com.girigiri.dao.models.Request;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

import static javafx.scene.input.KeyCode.T;

/**
 * Created by JianGuo on 6/25/16.
 * DAO service for model model {@link Request}
 */
public interface RequestRepository extends PagingAndSortingRepository<Request, Long>{

    List<Request> removeByCustomer(int customer);
}
