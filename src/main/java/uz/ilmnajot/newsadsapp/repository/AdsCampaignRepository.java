package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.AdsCampaign;

import java.util.List;

@Repository
public interface AdsCampaignRepository extends JpaRepository<AdsCampaign, Long> {
    List<AdsCampaign> findByStatus(AdsCampaign.Status status);
}

