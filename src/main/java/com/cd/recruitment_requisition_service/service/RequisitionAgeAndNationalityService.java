package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.param.RequisitionAgeAndNationalityParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;

import java.util.List;

public interface RequisitionAgeAndNationalityService extends BaseService<RequisitionAgeAndNationalityParam,Long> {

    BaseResponse findAllByParentId(Long parentId);
    BaseResponse savedAll(List<RequisitionAgeAndNationalityParam> requisitionAgeAndNationalityParams);
}
