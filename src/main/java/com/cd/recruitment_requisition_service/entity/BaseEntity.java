package com.cd.recruitment_requisition_service.entity;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class BaseEntity {

  //  @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdDateTime;

  //  @CreatedDate
    @Column(updatable = false)
    protected LocalDate createdDate;

   // @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    protected RecordStatus recordStatus;

    protected String createdBy;

    protected String actedUserName;

    protected String updatedBy;
}
