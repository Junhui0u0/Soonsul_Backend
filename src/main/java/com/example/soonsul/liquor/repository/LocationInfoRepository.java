package com.example.soonsul.liquor.repository;

import com.example.soonsul.liquor.entity.LocationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationInfoRepository extends JpaRepository<LocationInfo, Long> {
    List<LocationInfo> findByBrewery(String brewery);
}
