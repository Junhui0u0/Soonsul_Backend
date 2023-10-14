package com.example.soonsul.liquor.repository;

import com.example.soonsul.liquor.entity.Comment;
import com.example.soonsul.liquor.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Integer countByReview(Review review);
    void deleteAllByUpperCommentId(Long upperCommentId);

    @Query(nativeQuery = true,
            value="SELECT * FROM comment c WHERE c.review_id = :reviewId" +
                    " AND c.comment_id = c.upper_comment_id" +
                    " ORDER BY c.comment_id DESC")
    Page<Comment> findAllByLatest(Pageable pageable, @Param("reviewId") Long reviewId);

    @Query(nativeQuery = true,
            value="SELECT * FROM comment c WHERE c.upper_comment_id = :upperCommentId" +
                    " AND c.comment_id <> :upperCommentId" +
                    " ORDER BY c.comment_id DESC LIMIT 2")
    List<Comment> findAllByUpperComment(@Param("upperCommentId") Long upperCommentId);


    @Query(nativeQuery = true,
            value="SELECT * FROM comment c WHERE c.upper_comment_id = :upperCommentId" +
                    " AND c.comment_id <> :upperCommentId" +
                    " ORDER BY c.comment_id DESC")
    Page<Comment> findByUpperComment(Pageable pageable, @Param("upperCommentId") Long upperCommentId);

    List<Comment> findAllByUpperCommentId(Long upperCommentId);
}
