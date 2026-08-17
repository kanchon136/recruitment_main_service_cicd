package com.cd.recruitment_requisition_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testing")
public class Test {

    public String testing(){
        return  "Recruitment Service is Running with CICD pipiline";
    }
}
