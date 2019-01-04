
package com.mylive.project.loggy.elastic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;



@Repository()
public interface ElasticEventRepository extends ElasticsearchRepository<ElasticEvent,String>{

    Page<ElasticEvent> findById(String Id, Pageable pageable);

}
