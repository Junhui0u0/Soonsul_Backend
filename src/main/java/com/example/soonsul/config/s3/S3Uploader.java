package com.example.soonsul.config.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.soonsul.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String upload(MultipartFile multipartFile, String dirName){
        try{
            File uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new UploadFileNotExist("upload file not exist", ErrorCode.UPLOAD_FILE_NOT_EXIST));
            return upload(uploadFile, dirName);
        }catch (Exception e){
            throw new FileUpload("file upload error", ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }


    public void deleteFile(String fileName){
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }


    public String liquorMainUpload(MultipartFile multipartFile){
        try{
            File uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new UploadFileNotExist("upload file not exist", ErrorCode.UPLOAD_FILE_NOT_EXIST));
            return liquorMainUpload(uploadFile);
        }catch (Exception e){
            throw new FileUpload("file upload error", ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    private String liquorMainUpload(File uploadFile) {
        String fileName = "liquor/main/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }


    public String promotionUpload(MultipartFile multipartFile, String category){
        try{
            File uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new UploadFileNotExist("upload file not exist", ErrorCode.UPLOAD_FILE_NOT_EXIST));
            return promotionUpload(uploadFile, category);
        }catch (Exception e){
            throw new FileUpload("file upload error", ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    private String promotionUpload(File uploadFile, String category) {
        String fileName = "promotion/" + category + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }
}
