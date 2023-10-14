package com.example.soonsul.inquiry;

import com.example.soonsul.config.s3.S3Uploader;
import com.example.soonsul.inquiry.dto.InquiryRequest;
import com.example.soonsul.inquiry.entity.Inquiry;
import com.example.soonsul.inquiry.entity.InquiryImage;
import com.example.soonsul.inquiry.entity.InquiryType;
import com.example.soonsul.inquiry.repository.InquiryImageRepository;
import com.example.soonsul.inquiry.repository.InquiryRepository;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final UserUtil userUtil;
    private final S3Uploader s3Uploader;
    private final InquiryRepository inquiryRepository;
    private final InquiryImageRepository inquiryImageRepository;


    @Transactional
    public void postInquiry(InquiryRequest request){
        final User user= userUtil.getUserByAuthentication();
        final Inquiry inquiry= Inquiry.builder()
                .category(InquiryType.valueOf(request.getCategory()))
                .email(request.getEmail())
                .title(request.getTitle())
                .content(request.getContent())
                .createdDate(LocalDateTime.now())
                .user(user)
                .build();
        inquiryRepository.save(inquiry);

        if(request.getImages()==null) return;
        for(MultipartFile image: request.getImages()){
            final InquiryImage inquiryImage= InquiryImage.builder()
                    .image(s3Uploader.upload(image, "inquiry"))
                    .inquiry(inquiry)
                    .build();
            inquiryImageRepository.save(inquiryImage);
        }
    }
}