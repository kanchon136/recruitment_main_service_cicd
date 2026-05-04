package com.cd.recruitment_requisition_service.service;

 import com.cd.recruitment_requisition_service.utils.BaseResponse;

public interface RequisitionModuleCountService {
    BaseResponse findALlRequisitionModuleCountData(Long parenTRequisitionId);
}
