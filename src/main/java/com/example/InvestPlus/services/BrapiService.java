package com.example.InvestPlus.services;

import com.example.InvestPlus.responses.BrapiListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BrapiService {

    private final WebClient client;

    public BrapiService(WebClient.Builder builder, @Value("${brapi.base-url}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    public BrapiListResponse listarAtivos(String tipo, int page, int limit, String sort, String dir) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote/list")
                        .queryParam("type", tipo)
                        .queryParam("page", page)
                        .queryParam("limit", limit)
                        .queryParam("sortBy", sort)
                        .queryParam("sortOrder", dir)
                        .build())
                .retrieve()
                .bodyToMono(BrapiListResponse.class)
                .block();
    }
}
