package com.cd.recruitment_requisition_service.service;

 import com.cd.recruitment_requisition_service.param.RequisitionApprovedChannelParam;
 import com.cd.recruitment_requisition_service.utils.BaseResponse;

 import java.util.List;

public interface RequisitionApprovedChannelService extends BaseService<RequisitionApprovedChannelParam,Long> {

    BaseResponse findByRequisitionMasterId(Long masterId);
    BaseResponse saveAll(List<RequisitionApprovedChannelParam> requisitionApprovedChannelParams);

}
