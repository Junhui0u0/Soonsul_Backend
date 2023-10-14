package com.example.soonsul.main;

import com.example.soonsul.main.dto.MainBannerDto;
import com.example.soonsul.main.entity.BannerZzim;
import com.example.soonsul.main.entity.MainBanner;
import com.example.soonsul.main.exception.MainBannerNotExist;
import com.example.soonsul.main.repository.BannerZzimRepository;
import com.example.soonsul.main.repository.MainBannerRepository;
import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final UserUtil userUtil;
    private final MainBannerRepository mainBannerRepository;
    private final BannerZzimRepository bannerZzimRepository;


    @Transactional(readOnly = true)
    public List<MainBannerDto> getMainBannerList(){
        final List<MainBanner> list= mainBannerRepository.findAll();

        final List<MainBannerDto> result= new ArrayList<>();
        for(MainBanner banner: list){
            final MainBannerDto dto= MainBannerDto.builder()
                    .mainBannerId(banner.getMainBannerId())
                    .thumbnail(banner.getThumbnail())
                    .flagZzim(null)
                    .title(banner.getTitle())
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public MainBannerDto getMainBanner(Long mainBannerId){
        final User user= userUtil.getUserByAuthentication();
        final MainBanner banner= mainBannerRepository.findById(mainBannerId)
                .orElseThrow(()-> new MainBannerNotExist("main banner not exist", ErrorCode.MAIN_BANNER_NOT_EXIST));

        return MainBannerDto.builder()
                .mainBannerId(banner.getMainBannerId())
                .thumbnail(banner.getThumbnail())
                .content(banner.getContent())
                .flagZzim(bannerZzimRepository.existsByUserAndMainBanner(user, banner))
                .title(banner.getTitle())
                .build();
    }


    @Transactional
    public void postBannerZzim(Long mainBannerId){
        final User user= userUtil.getUserByAuthentication();
        final MainBanner banner= mainBannerRepository.findById(mainBannerId)
                .orElseThrow(()-> new MainBannerNotExist("main banner not exist", ErrorCode.MAIN_BANNER_NOT_EXIST));

        final BannerZzim zzim= BannerZzim.builder()
                .user(user)
                .mainBanner(banner)
                .build();
        bannerZzimRepository.save(zzim);
    }


    @Transactional
    public void deleteBannerZzim(Long mainBannerId){
        final User user= userUtil.getUserByAuthentication();
        final MainBanner banner= mainBannerRepository.findById(mainBannerId)
                .orElseThrow(()-> new MainBannerNotExist("main banner not exist", ErrorCode.MAIN_BANNER_NOT_EXIST));

        bannerZzimRepository.deleteByUserAndMainBanner(user, banner);
    }
}
