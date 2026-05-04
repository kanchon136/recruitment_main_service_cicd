package com.cd.recruitment_requisition_service.cumtomException;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
