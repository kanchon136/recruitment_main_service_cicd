package com.cd.recruitment_requisition_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableScheduling
@Slf4j
public class RecruitmentRequisitionService {

	public static void main(String[] args) {
		SpringApplication.run(RecruitmentRequisitionService.class, args);
		log.info("<===== RECRUITMENT REQUISITION SERVICE START ======>");
		//System.out.println("<===== RECRUITMENT REQUISITION SERVICE START ======>");
	}

}
