package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.Tag;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByCode(String code);
    boolean existsByCode(String code);
    List<Tag> findByIsActiveTrue();
    Optional<Tag> findTagByIdAndIsActiveTrue(Long id);
}

