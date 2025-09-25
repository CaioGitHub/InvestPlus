package com.example.InvestPlus.responses;

import com.example.InvestPlus.dtos.StockDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BrapiListResponse {
    private List<StockDto> stocks;

    @JsonProperty("currentPage")
    private int currentPage;

    @JsonProperty("totalPages")
    private int totalPages;

    @JsonProperty("itemsPerPage")
    private int itemsPerPage;

    @JsonProperty("totalCount")
    private int totalCount;

    private boolean hasNextPage;
}