package com.example.InvestPlus.responses;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BrapiListResponse {

    @JsonAlias({ "stocks", "results" })
    private List<BrapiStockResponse> stocks;

    private int currentPage;
    private int totalPages;
    private int itemsPerPage;
    private int totalCount;
    private boolean hasNextPage;
}

