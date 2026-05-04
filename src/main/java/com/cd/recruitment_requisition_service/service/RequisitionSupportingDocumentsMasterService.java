package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.param.RequisitionSupportingDocumentsMasterParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RequisitionSupportingDocumentsMasterService extends BaseService<RequisitionSupportingDocumentsMasterParam,Long> {
    BaseResponse findByRecruitmentRequisitionMasterId(Long masterId);
    BaseResponse findALlByRecordStatusAndCreatedBy(String logInUserId);
    BaseResponse findAllByRecordStatusAndCreatedByAndRecruitmentRequisitionMaster_idAndRecordStatusAndCreatedBy(
            Long masterId, String logInUserId);
    BaseResponse update(Long masterId, RequisitionSupportingDocumentsMasterParam param,
                        Map<Long, MultipartFile> existingFiles, List<MultipartFile> newFiles);

    BaseResponse deleteById(Long docsMstId, List<Long> childIds, String currentUserId);
}
