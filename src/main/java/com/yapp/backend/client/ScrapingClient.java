package com.yapp.backend.client;

import com.yapp.backend.service.dto.ScrapingRequest;
import com.yapp.backend.service.dto.ScrapingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "${scraping.server.client}",
    url = "${scraping.server.url}"
)
public interface ScrapingClient {
    
    @PostMapping("/scrape")
    ScrapingResponse scrapeData(@RequestBody ScrapingRequest request);
}