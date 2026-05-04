package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.param.PIDResetParam;
import com.cd.recruitment_requisition_service.param.RequisitionActionParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import jakarta.transaction.Transactional;


public interface RequisitionActionService {
    @Transactional
    BaseResponse takeAction(RequisitionActionParam request);

    BaseResponse getRequisitionHistory(Long requisitionId);

    BaseResponse getAllRequisitionActions(Long requisitionId);

    // URS Logic: For manually resetting the Process Initiation Date (PID)
    @Transactional
    BaseResponse resetProcessInitiationDate(PIDResetParam request);

    BaseResponse getGroupActionDetailsByLayer(Long masterRequisitionId, Integer layerPosition);
}
