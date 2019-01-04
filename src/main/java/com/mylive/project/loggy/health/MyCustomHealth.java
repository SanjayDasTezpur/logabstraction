package com.mylive.project.loggy.health;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sanjayda on 7/12/18 at 1:03 PM
 */

/*
    This is custom test class for Custom/User define Health check
 */
@RestController
public class MyCustomHealth {

    @RequestMapping("/my-health-check")
    public String getCustomHealth()
    {
        return "{\"status\":\"UP\"}";
    }
}
