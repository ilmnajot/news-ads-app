package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.AdsAssignment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdsAssignmentRepository extends JpaRepository<AdsAssignment, Long> {
    
    @Query("SELECT a FROM AdsAssignment a WHERE a.placement.code = :placementCode " +
           "AND a.isActive = true AND a.placement.isActive = true " +
           "AND a.campaign.status = 'ACTIVE' AND a.creative.isActive = true " +
           "AND (a.startAt IS NULL OR a.startAt <= :now) " +
           "AND (a.endAt IS NULL OR a.endAt > :now) " +
           "AND (a.campaign.startAt IS NULL OR a.campaign.startAt <= :now) " +
           "AND (a.campaign.endAt IS NULL OR a.campaign.endAt > :now)")
    List<AdsAssignment> findActiveAssignmentsByPlacement(@Param("placementCode") String placementCode,
                                                          @Param("now") LocalDateTime now);
    
    List<AdsAssignment> findByPlacementId(Long placementId);
    List<AdsAssignment> findByCampaignId(Long campaignId);

    @Query("select a from AdsAssignment as a where a.placement.code=:code")
    List<AdsAssignment> findActiveAssignmentsForPlacement(@Param("code") String code);
}

