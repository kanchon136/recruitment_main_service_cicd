package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionApprovedChannel;
import com.cd.recruitment_requisition_service.enums.AuthorizationType;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequisitionApprovedChannelRepository extends JpaRepository<RequisitionApprovedChannel, Long> {

    Optional<RequisitionApprovedChannel> findByIdAndRecordStatus(Long id , RecordStatus recordStatus);
    List<RequisitionApprovedChannel> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionApprovedChannel> findAllByRecordStatus(RecordStatus recordStatus , Pageable pageable);
    List<RequisitionApprovedChannel> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long masterId);
    List<RequisitionApprovedChannel> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus, Long masterId, RecordStatus masterStatus);

    Optional<RequisitionApprovedChannel> findByLayerPositionAndMemberCode(Integer nextLayer, String memberCode);
   // Optional<RequisitionApprovedChannel> findTop1ByLayerPositionAndMemberCode(Integer layerPosition, String memberCode);

   // List<Long> findAllByRecruitmentRequisitionMaster_idAndLayerPosition(Long masterId, Integer layerPosition);
    List<Long> findAllByRecordStatusAndLayerPositionAndRecruitmentRequisitionMaster_idAndRecordStatus(RecordStatus recordStatus,Integer layerPosition,Long masterId,RecordStatus parentStatus);
    /**
     * URS Logic: Finds the single channel entity representing the next layer's role group.
     * Used for determining nextRole/nextLayerPosition in Master (e.g., for Finance HOD skip logic).
     * findTop1 is used because we only need one of the multiple channels to get the member/role string.
     */
    Optional<RequisitionApprovedChannel> findTopByLayerPositionAndMemberCode(Integer layerPosition, String memberCode);


    @Query("SELECT rac.id FROM RequisitionApprovedChannel rac " +
            "WHERE rac.recruitmentRequisitionMaster.id = :masterId AND rac.layerPosition = :layerPosition")
    List<Long> findAllChannelIdsByMasterIdAndLayerPosition(
            @Param("masterId") Long masterId,
            @Param("layerPosition") Integer layerPosition);

    List<RequisitionApprovedChannel> findAllByRecruitmentRequisitionMasterIdAndLayerPosition(Long id, Integer nextLayer);

    Optional<RequisitionApprovedChannel> findTopByRecruitmentRequisitionMasterIdAndLayerPosition(Long id, Integer fromLayer);

    List<RequisitionApprovedChannel> findAllByRecruitmentRequisitionMasterId(Long mId);

    Optional<RequisitionApprovedChannel> findByRecruitmentRequisitionMasterIdAndLayerPositionAndMemberCode(Long id, Integer currentLayer, String actedBy);

    void deleteAllByRecruitmentRequisitionMaster_Id(Long masterId);



    boolean existsByRecruitmentRequisitionMaster_Id(Long masterId);


    List<RequisitionApprovedChannel> findAllByRecruitmentRequisitionMasterIdOrderByLayerPositionAsc(Long masterId);

    boolean existsByRecruitmentRequisitionMasterIdAndAuthorizationType(Long recruitmentRequisitionMasterId, AuthorizationType authorizationType);
}
