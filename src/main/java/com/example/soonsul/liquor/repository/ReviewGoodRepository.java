package com.example.soonsul.liquor.repository;

import com.example.soonsul.liquor.entity.Review;
import com.example.soonsul.liquor.entity.ReviewGood;
import com.example.soonsul.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewGoodRepository extends JpaRepository<ReviewGood, Long> {
    Integer countByReview(Review review);
    boolean existsByReviewAndUser(Review review, User user);
    void deleteByReviewAndUser(Review review, User user);
    Optional<ReviewGood> findByReviewAndUser(Review review, User user);
}
