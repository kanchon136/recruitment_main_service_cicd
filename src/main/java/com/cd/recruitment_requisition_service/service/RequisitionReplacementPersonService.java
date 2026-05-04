package com.cd.recruitment_requisition_service.service;

 import com.cd.recruitment_requisition_service.param.RequisitionReplacementPersonParam;
 import com.cd.recruitment_requisition_service.utils.BaseResponse;

 import java.util.List;

public interface RequisitionReplacementPersonService extends BaseService<RequisitionReplacementPersonParam,Long> {
    BaseResponse findAllByRequisitionMasterId(Long masterId);
   BaseResponse saveAll(List<RequisitionReplacementPersonParam> requisitionReplacementPersonParams);

}
