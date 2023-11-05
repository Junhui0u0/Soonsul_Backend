package com.example.soonsul.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final GoogleSheetsService googleSheetsService;

    @Value("${google-sheet-id}")
    private String spreadsheetId;

    private final List<String> rangeList= Arrays.asList("탁주!A2:O265", "약주/청주!A2:O139",
            "증류식소주!A2:O129", "과실주!A2:O125", "기타주류!A2:O6");


    @Scheduled(cron = "0 0 0 * * *")
    private void SheetDataToDB() throws IOException {
        for(String range: rangeList){
            googleSheetsService.postLiquor(spreadsheetId, range);
        }
    }

}
