package com.example.InvestPlus.services;

import com.example.InvestPlus.dtos.StockDto;
import com.example.InvestPlus.responses.BrapiListResponse;
import com.example.InvestPlus.responses.BrapiStockResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public StockDto buscarPorCodigo(String codigo) {
        BrapiListResponse response = client.get()
                .uri(uriBuilder -> uriBuilder.path("/quote/{codigo}").build(codigo))
                .retrieve()
                .bodyToMono(BrapiListResponse.class)
                .block();

        if (response == null || response.getStocks() == null || response.getStocks().isEmpty()) {
            try {
                String raw = client.get()
                        .uri(uriBuilder -> uriBuilder.path("/quote/{codigo}").build(codigo))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                System.out.println("BRAPI /quote/" + codigo + " raw response: " + raw);
            } catch (Exception e) {
                System.out.println("Erro ao obter raw response: " + e.getMessage());
            }
            return null;
        }

        return toStockDto(response.getStocks().get(0));
    }

    public List<StockDto> listarAtivosDto(String tipo, int page, int limit, String sort, String dir) {
        return toStockDtoList(listarAtivos(tipo, page, limit, sort, dir));
    }

    public List<StockDto> toStockDtoList(BrapiListResponse response) {
        if (response == null || response.getStocks() == null) return Collections.emptyList();
        return response.getStocks().stream()
                .map(this::toStockDto)
                .collect(Collectors.toList());
    }

    private StockDto toStockDto(BrapiStockResponse br) {
        if (br == null) return null;

        StockDto dto = new StockDto();
        dto.setStock(br.getStock());
        dto.setName(br.getName() != null ? br.getName() : "");
        dto.setClose(br.getClose());
        dto.setChange(br.getChange());
        dto.setVolume(br.getVolume());
        dto.setMarketCap(br.getMarketCap());

        String logo = br.getLogo();
        if (logo == null || logo.isBlank()) {
            logo = "https://icons.brapi.dev/icons/" + (br.getStock() != null ? br.getStock() : "BRAPI") + ".svg";
        }
        dto.setLogo(logo);

        dto.setSector(br.getSector() != null ? br.getSector() : "");
        dto.setType(br.getType() != null ? br.getType() : "stock");

        return dto;
    }
}
