package com.example.soonsul.liquor.repository;

import com.example.soonsul.liquor.entity.SalePlaceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalePlaceInfoRepository extends JpaRepository<SalePlaceInfo, Long> {
    Optional<SalePlaceInfo> findByName(String name);
}
