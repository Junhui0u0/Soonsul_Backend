package com.example.soonsul.main.repository;

import com.example.soonsul.main.entity.BannerZzim;
import com.example.soonsul.main.entity.MainBanner;
import com.example.soonsul.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerZzimRepository extends JpaRepository<BannerZzim, Long> {
    boolean existsByUserAndMainBanner(User user, MainBanner mainBanner);
    void deleteByUserAndMainBanner(User user, MainBanner mainBanner);
}
