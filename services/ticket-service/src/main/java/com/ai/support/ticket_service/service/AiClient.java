package com.ai.support.ticket_service.service;

import com.ai.support.ticket_service.dto.AiClassifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestTemplate restTemplate;

    public AiClassifyResponse classify(String title, String description) {
        return restTemplate.postForObject(
                "http://ai-orchestrator:4000/ai/classify",
                Map.of("title", title, "description", description),
                AiClassifyResponse.class
        );
    }
}
