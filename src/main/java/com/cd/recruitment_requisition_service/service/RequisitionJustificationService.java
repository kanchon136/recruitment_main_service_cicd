package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.param.RequisitionJustificationParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;

import java.util.List;

public interface RequisitionJustificationService extends BaseService<RequisitionJustificationParam,Long>{
    BaseResponse findAllByRequisitionMasterId(Long masterId);
    BaseResponse savedAll(List<RequisitionJustificationParam> requisitionJustificationParams);
}
