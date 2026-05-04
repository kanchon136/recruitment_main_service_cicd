package com.cd.recruitment_requisition_service.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {
    PENDING("PENDING"),
    CANCEL("CANCELLED"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    NOTFOUND("NOT FOUND"),
    ALREADY_EXISTS("ALREADY EXISTS");

    private final String status;

    ResponseEnum(String status){
        this.status = status;
    }
}
