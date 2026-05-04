package com.cd.recruitment_requisition_service.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseParam {

    private String actedUserCode;
    private String actedUserName;
  //  private String createdBy;
    private String updatedBy;
}
