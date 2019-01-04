
package com.mylive.project.loggy.elastic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * Created by sanjayda on 3/1/18 at 12:57 PM
 */



@Component()
public class ElasticEventServiceImpl implements ElasticEventService
{
    private ElasticEventRepository eerepo;

    @Autowired
    public void setElasticEventRepository(ElasticEventRepository es){
        this.eerepo = es;
    }

    @Override
    public ElasticEvent save(ElasticEvent eevent) {
        return eerepo.save(eevent);
    }

    public Optional<ElasticEvent> findOne(String Id) {
        return eerepo.findById(Id);
    }

    public Iterable<ElasticEvent> findAll() {
        return eerepo.findAll();
    }

    @Override
    public Page<ElasticEvent> findById(String Id, PageRequest pageRequest) {
        return eerepo.findById(Id,pageRequest);
    }

}
