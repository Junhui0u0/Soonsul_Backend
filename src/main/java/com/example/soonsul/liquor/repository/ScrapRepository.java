package com.example.soonsul.liquor.repository;

import com.example.soonsul.liquor.entity.Comment;
import com.example.soonsul.liquor.entity.Liquor;
import com.example.soonsul.liquor.entity.Scrap;
import com.example.soonsul.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    void deleteByUserAndLiquor(User user, Liquor liquor);
    boolean existsByUserAndLiquor(User user, Liquor liquor);

    @Query(nativeQuery = true,
            value="SELECT * FROM scrap s inner join liquor l ON s.liquor_id = l.liquor_id" +
                    " WHERE s.user_id = :userId" +
                    " ORDER BY s.scrap_id DESC")
    Page<Scrap> findByDate(Pageable pageable, @Param("userId") String userId);


    @Query(nativeQuery = true,
            value="SELECT * FROM scrap s inner join liquor l ON s.liquor_id = l.liquor_id" +
                    " WHERE s.user_id = :userId" +
                    " ORDER BY l.average_rating DESC")
    Page<Scrap> findByStar(Pageable pageable, @Param("userId") String userId);


    @Query(nativeQuery = true,
            value="SELECT * FROM scrap s inner join liquor l ON s.liquor_id = l.liquor_id" +
                    " WHERE s.user_id = :userId" +
                    " ORDER BY l.lowest_price")
    Page<Scrap> findByLowestCost(Pageable pageable, @Param("userId") String userId);

    @Query(nativeQuery = true,
            value="SELECT * FROM scrap s inner join liquor l ON s.liquor_id = l.liquor_id" +
                    " WHERE s.user_id = :userId" +
                    " ORDER BY l.lowest_price DESC")
    Page<Scrap> findByHighestCost(Pageable pageable, @Param("userId") String userId);

    Integer countByUser(User user);
}
