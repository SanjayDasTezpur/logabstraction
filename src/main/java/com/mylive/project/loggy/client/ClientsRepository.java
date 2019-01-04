package com.mylive.project.loggy.client;

import org.springframework.data.repository.CrudRepository;

public interface ClientsRepository extends CrudRepository<Client, Long> {
    Client findById(String id);
}