package com.example.soonsul.user.repository;

import com.example.soonsul.liquor.entity.Liquor;
import com.example.soonsul.liquor.entity.Review;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalEvaluationRepository extends JpaRepository<PersonalEvaluation,Long> {

    Optional<PersonalEvaluation> findByUserAndLiquor(User user, Liquor liquor);
    Long countByLiquor(Liquor liquor);
    Integer countByUser(User user);

    @Query(nativeQuery = true,
            value = "SELECT * FROM personal_evaluation p WHERE p.user_id = :userId " +
                    "AND p.liquor_id = :liquorId ")
    Optional<PersonalEvaluation> findPersonalEvaluation(@Param("userId") String userId, @Param("liquorId") String liquorId);

    @Query(nativeQuery = true,
            value="SELECT * FROM personal_evaluation p WHERE p.user_id = :userId" +
                    " ORDER BY p.personal_evaluation_id DESC")
    Page<PersonalEvaluation> findAll(Pageable pageable, @Param("userId") String userId);

    void deleteByUserAndLiquor(User user, Liquor liquor);
}
