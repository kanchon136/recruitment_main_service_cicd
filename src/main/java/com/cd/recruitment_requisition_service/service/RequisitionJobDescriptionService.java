package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.param.RequisitionJobDescriptionParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;

import java.util.List;

public interface RequisitionJobDescriptionService extends BaseService<RequisitionJobDescriptionParam,Long> {


    BaseResponse findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(Long parentMasterId);

    BaseResponse saveAll(List<RequisitionJobDescriptionParam> jobDescriptionParamList);

    BaseResponse deleteByIds(Long descriptionId, Long listId, Long taskId);
}
