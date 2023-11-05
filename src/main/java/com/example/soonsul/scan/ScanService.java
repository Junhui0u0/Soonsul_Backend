package com.example.soonsul.scan;

import com.example.soonsul.config.s3.S3Uploader;
import com.example.soonsul.liquor.entity.Liquor;
import com.example.soonsul.liquor.repository.LiquorRepository;
import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.scan.dto.ScanDto;
import com.example.soonsul.scan.exception.ScanNotExist;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.repository.PersonalEvaluationRepository;
import com.example.soonsul.util.LiquorUtil;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScanService {
    private final ScanRepository scanRepository;
    private final LiquorRepository liquorRepository;
    private final UserUtil userUtil;
    private final LiquorUtil liquorUtil;
    private final S3Uploader s3Uploader;
    private final PersonalEvaluationRepository personalEvaluationRepository;


    @Value("${cloud.aws.s3.bucket.url}")
    private String AWS_S3_BUCKET_URL;


    @Transactional(readOnly = true)
    public String getLiquor(String name){
        final Optional<Liquor> liquor= liquorRepository.findByName(name);
        return (liquor.isPresent()) ? liquor.get().getLiquorId() : "";
    }


    @Transactional
    public void postScan(String liquorId, MultipartFile image){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final Scan scan= Scan.builder()
                .scanDate(LocalDate.now())
                .imageUrl(s3Uploader.upload(image,"scan"))
                .user(user)
                .liquor(liquor)
                .build();
        scanRepository.save(scan);
    }


    @Transactional(readOnly = true)
    public List<ScanDto> getScanList(Pageable pageable){
        final User user= userUtil.getUserByAuthentication();
        final List<Scan> scanList= scanRepository.findAllByUser(pageable, user.getUserId()).toList();

        final List<ScanDto> result= new ArrayList<>();
        for(Scan s: scanList){
            final Liquor liquor= s.getLiquor();

            final String liquorCategory= liquorUtil.getCodeName(liquor.getLiquorCategory());

            boolean flag= true;
            final Optional<PersonalEvaluation> evaluation= personalEvaluationRepository.findByUserAndLiquor(user, liquor);
            if(evaluation.isEmpty()) flag=false;

            final ScanDto dto= ScanDto.builder()
                    .scanId(s.getScanId())
                    .scanDate(s.getScanDate())
                    .imageUrl(s.getImageUrl())
                    .liquorId(liquor.getLiquorId())
                    .name(liquor.getName())
                    .liquorCategory(liquorCategory)
                    .brewery(liquor.getBrewery())
                    .averageRating(liquor.getAverageRating())
                    .flagEvaluation(flag)
                    .personalRating((flag) ? evaluation.get().getLiquorPersonalRating() : null)
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional
    public void deleteScan(Long scanId){
        final Scan scan= scanRepository.findById(scanId)
                .orElseThrow(()-> new ScanNotExist("scan not exist", ErrorCode.SCAN_NOT_EXIST));
        s3Uploader.deleteFile(scan.getImageUrl().substring(AWS_S3_BUCKET_URL.length()));
        scanRepository.deleteById(scanId);
    }

}
