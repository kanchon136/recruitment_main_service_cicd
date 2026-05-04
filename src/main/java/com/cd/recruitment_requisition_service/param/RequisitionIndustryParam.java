package com.cd.recruitment_requisition_service.param;

//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionIndustryParam extends BaseParam {
    private Long id;

   // @NotNull(message = "Recruitment Requisition Master ID is required")
    private Long recruitmentRequisitionMasterId;

   // @NotBlank(message = "Industry Code is required")
  //  @Size(max = 50, message = "Industry Code cannot exceed 50 characters")
    private String industryCode;

  //  @NotBlank(message = "Industry Name is required")
  //  @Size(max = 100, message = "Industry Name cannot exceed 100 characters")
    private String industryName;
}
