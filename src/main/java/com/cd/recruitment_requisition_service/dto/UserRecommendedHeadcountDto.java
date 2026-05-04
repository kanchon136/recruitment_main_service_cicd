package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRecommendedHeadcountDto {
    private String actedUserId;
    private String actedUserName;
    private Integer headcount;
}
