package com.example.soonsul.promotion;

import com.example.soonsul.main.entity.MainBanner;
import com.example.soonsul.main.repository.BannerZzimRepository;
import com.example.soonsul.main.repository.MainBannerRepository;
import com.example.soonsul.promotion.dto.PromotionDto;
import com.example.soonsul.promotion.entity.Promotion;
import com.example.soonsul.promotion.entity.Zzim;
import com.example.soonsul.promotion.exception.PromotionNotExist;
import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final UserUtil userUtil;
    private final PromotionRepository promotionRepository;
    private final ZzimRepository zzimRepository;
    private final MainBannerRepository mainBannerRepository;
    private final BannerZzimRepository bannerZzimRepository;


    @Transactional(readOnly = true)
    public List<PromotionDto> getPromotionList(String category){
        final User user= userUtil.getUserByAuthentication();
        final List<Promotion> list= promotionRepository.findAll();

        final List<PromotionDto> result= new ArrayList<>();
        for(Promotion p: list){
            if(p.getEndDate()!=null && LocalDate.now().isAfter(p.getEndDate())) continue;
            final PromotionDto dto= PromotionDto.builder()
                    .promotionId(p.getPromotionId())
                    .title(p.getTitle())
                    .content(p.getContent())
                    .beginDate(p.getBeginDate())
                    .endDate(p.getEndDate())
                    .location(p.getLocation())
                    .image(p.getImage())
                    .flagZzim(zzimRepository.existsByUserAndPromotion(user, p))
                    .build();
            result.add(dto);
        }
        if(category.equals("promotion")) return result;

        final List<MainBanner> bannerList= mainBannerRepository.findAll();
        for(MainBanner banner: bannerList){
            final PromotionDto dto= PromotionDto.builder()
                    .mainBannerId(banner.getMainBannerId())
                    .bannerContent(banner.getContent())
                    .bannerThumbnail(banner.getThumbnail())
                    .bannerName(banner.getTitle())
                    .flagZzim(bannerZzimRepository.existsByUserAndMainBanner(user, banner))
                    .build();
            result.add(dto);
        }

        return result;
    }


    @Transactional(readOnly = true)
    public PromotionDto getPromotion(Long promotionId){
        final User user= userUtil.getUserByAuthentication();
        final Promotion promotion= promotionRepository.findById(promotionId)
                .orElseThrow(()-> new PromotionNotExist("promotion not exist", ErrorCode.PROMOTION_NOT_EXIST));

        return PromotionDto.builder()
                .promotionId(promotion.getPromotionId())
                .title(promotion.getTitle())
                .content(promotion.getContent())
                .beginDate(promotion.getBeginDate())
                .endDate(promotion.getEndDate())
                .location(promotion.getLocation())
                .image(promotion.getImage())
                .flagZzim(zzimRepository.existsByUserAndPromotion(user, promotion))
                .build();
    }


    @Transactional
    public void postZzim(Long promotionId){
        final User user= userUtil.getUserByAuthentication();
        final Promotion promotion= promotionRepository.findById(promotionId)
                .orElseThrow(()-> new PromotionNotExist("promotion not exist", ErrorCode.PROMOTION_NOT_EXIST));

        final Zzim zzim= Zzim.builder()
                .user(user)
                .promotion(promotion)
                .build();
        zzimRepository.save(zzim);
    }


    @Transactional
    public void deleteZzim(Long promotionId){
        final User user= userUtil.getUserByAuthentication();
        final Promotion promotion= promotionRepository.findById(promotionId)
                .orElseThrow(()-> new PromotionNotExist("promotion not exist", ErrorCode.PROMOTION_NOT_EXIST));

        zzimRepository.deleteByUserAndPromotion(user, promotion);
    }

}
