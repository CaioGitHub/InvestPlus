package com.example.InvestPlus.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockDto {
    @JsonProperty("symbol")
    private String stock;

    @JsonProperty("name")
    private String name;

    @JsonProperty("close")
    private Double close;

    @JsonProperty("change")
    private Double change;

    @JsonProperty("volume")
    private Long volume;

    @JsonProperty("market_cap")
    private Long marketCap;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("sector")
    private String sector;

    @JsonProperty("type")
    private String type;
}
