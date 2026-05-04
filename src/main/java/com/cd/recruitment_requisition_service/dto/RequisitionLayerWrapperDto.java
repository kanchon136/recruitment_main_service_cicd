package com.cd.recruitment_requisition_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequisitionLayerWrapperDto {
    private Integer layerPosition;
    private String processTitle;
    private Boolean isStageMandatory;
    private Boolean isSkippable;
    private List<RequisitionMemberDto> members;
}
