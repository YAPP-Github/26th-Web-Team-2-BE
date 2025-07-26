package com.yapp.backend.service.impl;

import com.yapp.backend.client.ScrapingClient;
import com.yapp.backend.common.exception.CustomException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.service.ScrapingService;
import com.yapp.backend.service.dto.ScrapingRequest;
import com.yapp.backend.service.dto.ScrapingResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapingServiceImpl implements ScrapingService {

    private final ScrapingClient scrapingClient;

    @Override
    public ScrapingResponse scrapeAccommodationData(String url) {
        try {
            log.info("Requesting scraping for URL: {}", url);
            
            // 요청 바디 생성
            ScrapingRequest request = ScrapingRequest.builder()
                    .url(url)
                    .build();
            
            // FeignClient를 통해 외부 스크래핑 서버에 요청
            ScrapingResponse scrapingResponse = scrapingClient.scrapeData(request);
            
            if (scrapingResponse == null || !scrapingResponse.isSuccess()) {
                log.error("Scraping failed for URL: {}, response: {}", url, scrapingResponse);
                throw new CustomException(ErrorCode.SCRAPING_FAILED);
            }
            
            log.info("Scraping completed successfully for URL: {}", url);
            return scrapingResponse;
            
        } catch (FeignException e) {
            log.error("Network error while scraping URL: {}, status: {}", url, e.status(), e);
            throw new CustomException(ErrorCode.SCRAPING_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error while scraping URL: {}", url, e);
            throw new CustomException(ErrorCode.SCRAPING_FAILED);
        }
    }
}