package com.yapp.backend.service;

import com.yapp.backend.service.dto.ScrapingResponse;

public interface ScrapingService {
    /**
     * 외부 스크래핑 서버에 URL을 전송하여 숙소 정보를 스크래핑
     */
    ScrapingResponse scrapeAccommodationData(String url);
}