package com.cd.recruitment_requisition_service.service;

 import com.cd.recruitment_requisition_service.param.RequisitionIndustryParam;
 import com.cd.recruitment_requisition_service.utils.BaseResponse;

 import java.util.List;

public interface RequisitionIndustryService extends BaseService<RequisitionIndustryParam,Long> {

    BaseResponse findAllByParentId(Long parentId);
    BaseResponse saveAll(List<RequisitionIndustryParam> requisitionIndustryParams);
}
