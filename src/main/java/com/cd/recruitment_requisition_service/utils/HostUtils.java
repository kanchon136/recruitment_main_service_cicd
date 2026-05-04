package com.cd.recruitment_requisition_service.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HostUtils {

    @Value("${app.host-url}")
    private String hostUrl;

    private static String HOST_URL; // static copy for static methods

    @PostConstruct
    public void init() {
        HOST_URL = hostUrl; // copy injected value to static
    }

    public static String getHostUrl() {

        log.info("HOST_URL==================>"+HOST_URL);
        return HOST_URL;
    }

}
