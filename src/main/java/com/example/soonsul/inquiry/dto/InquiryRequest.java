package com.example.soonsul.inquiry.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class InquiryRequest {
    private String category;
    private String email;
    private String title;
    private String content;
    private List<MultipartFile> images;
}
