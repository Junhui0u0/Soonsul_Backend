package com.example.soonsul.liquor.repository;

import com.example.soonsul.liquor.entity.Liquor;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiquorRepository extends JpaRepository<Liquor, String> {
    Optional<Liquor> findByName(String name);

    @Query(nativeQuery = true,
            value="SELECT l.name FROM liquor l")
    List<String> findAllName();

    @Query(nativeQuery = true,
            value="SELECT l.liquor_id FROM liquor l")
    List<String> findAllId();

    @Query(nativeQuery = true,
            value="SELECT * FROM liquor l WHERE l.name like %:name% order by l.name")
    List<Liquor> findSearch(@Param("name") String name);

}
