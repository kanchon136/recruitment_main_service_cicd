package com.cd.recruitment_requisition_service.dto;

import com.cd.recruitment_requisition_service.enums.RecordStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntityDto {
    private LocalDateTime createdDateTime;
    private LocalDate createdDate;
    private LocalDateTime updatedAt;
    private RecordStatus recordStatus;
    private String createdBy;
    protected String actedUserName;
    private String updatedBy;
}
