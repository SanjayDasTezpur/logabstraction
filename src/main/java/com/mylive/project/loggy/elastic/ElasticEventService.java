
package com.mylive.project.loggy.elastic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

/**
 * Created by sanjayda on 3/1/18 at 11:34 AM
 */


public interface ElasticEventService {
    ElasticEvent save(ElasticEvent eevent);
    Optional<ElasticEvent> findOne(String Id);
    Iterable<ElasticEvent> findAll();
    Page<ElasticEvent> findById(String Id, PageRequest pageRequest);
}

