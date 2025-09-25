package com.example.InvestPlus.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockDto {
    private String stock;

    private String name;

    private Double close;

    private Double change;

    private Long volume;

    @JsonProperty("market_cap")
    private Long marketCap;

    private String logo;

    private String sector;

    private String type;
}
