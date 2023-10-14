package com.example.soonsul.main.repository;

import com.example.soonsul.main.entity.MainBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainBannerRepository extends JpaRepository<MainBanner, Long> {
}
