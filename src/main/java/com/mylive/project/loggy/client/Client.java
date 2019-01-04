package com.mylive.project.loggy.client;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
public class Client {

    @Id
    String id;
    String server;
    String name;
    int is_connected;

    @Temporal(TemporalType.TIMESTAMP)
    Date created_date;

    @Temporal(TemporalType.TIMESTAMP)
    Date last_connection;

    @ElementCollection
    @CollectionTable(name="subscriber")
    List<String> lstErrorSubscriber;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIs_connected() {
        return is_connected;
    }

    public void setIs_connected(int is_connected) {
        this.is_connected = is_connected;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public Date getLast_connection() {
        return last_connection;
    }

    public void setLast_connection(Date last_connection) {
        this.last_connection = last_connection;
    }

    public List<String> getLstErrorSubscriber() {
        return lstErrorSubscriber;
    }

    public void setLstErrorSubscriber(List<String> lstErrorSubscriber) {
        this.lstErrorSubscriber = lstErrorSubscriber;
    }

    public void addErrorSubscriber(String subsciber) {
        lstErrorSubscriber.add(subsciber);
    }

}
