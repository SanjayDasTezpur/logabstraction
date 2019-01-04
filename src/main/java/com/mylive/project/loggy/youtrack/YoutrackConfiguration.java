package com.mylive.project.loggy.youtrack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YoutrackConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(YoutrackConfiguration.class);
    
    @Value("${youtrack.host}")
    private String YOUTRACK_HOST;

    @Value("${youtrack.port}")
    private String YOUTRACK_PORT;

    private String youtrackEndPoint;
                                                                              
    @Bean
    public String buildYoutrack()
    {
        String sValue = getYOUTRACK_HOST() + ":" + getYOUTRACK_PORT();
        logger.info("Youtrack plugin configured with endpoint " + sValue);
        this.setYoutrackEndPoint(sValue);
        return sValue;
    }
                                             
    public String getYOUTRACK_HOST() {
        return YOUTRACK_HOST;
    }

    public void setYOUTRACK_HOST(String YOUTRACK_HOST) {
        this.YOUTRACK_HOST = YOUTRACK_HOST;
    }

    public String getYOUTRACK_PORT() {
        return YOUTRACK_PORT;
    }

    public void setYOUTRACK_PORT(String YOUTRACK_PORT) {
        this.YOUTRACK_PORT = YOUTRACK_PORT;
    }

    public String getYoutrackEndPoint() {
        return youtrackEndPoint;
    }

    public void setYoutrackEndPoint(String youtrackEndPoint) {
        this.youtrackEndPoint = youtrackEndPoint;
    }
}
