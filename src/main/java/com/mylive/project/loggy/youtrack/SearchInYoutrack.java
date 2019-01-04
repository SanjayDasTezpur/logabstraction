package com.mylive.project.loggy.youtrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchInYoutrack {

    @Autowired
    YoutrackConfiguration  yConf;

    public  boolean searchBugIfExists(String sDescription)
    {
        return true;
    }
}
