package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.AdsPlacement;

import java.util.Optional;

@Repository
public interface AdsPlacementRepository extends JpaRepository<AdsPlacement, Long> {
    Optional<AdsPlacement> findByCode(String code);
    boolean existsByCodeAndIsActiveTrue(String code);
}

