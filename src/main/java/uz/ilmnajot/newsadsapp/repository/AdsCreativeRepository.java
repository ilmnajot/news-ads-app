package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.AdsCreative;

import java.util.List;

@Repository
public interface AdsCreativeRepository extends JpaRepository<AdsCreative, Long> {
    List<AdsCreative> findByCampaignId(Long campaignId);
    List<AdsCreative> findByCampaignIdAndIsActiveTrue(Long campaignId);
}

