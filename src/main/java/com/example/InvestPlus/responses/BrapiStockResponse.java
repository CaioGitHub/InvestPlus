package com.example.InvestPlus.responses;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BrapiStockResponse {

    
    @JsonAlias({ "stock", "symbol" })
    private String stock;

    
    @JsonAlias({ "name", "shortName", "longName" })
    private String name;

    
    @JsonAlias({ "close", "regularMarketPrice" })
    private Double close;

    
    @JsonAlias({ "change", "regularMarketChangePercent" })
    private Double change;

    
    @JsonAlias({ "volume", "regularMarketVolume" })
    private Long volume;

    
    @JsonAlias({ "market_cap", "marketCap", "market_capitalization" })
    private Long marketCap;

    
    @JsonAlias({ "logo", "logoUrl", "logo_url" })
    private String logo;

    
    @JsonAlias({ "sector", "industry" })
    private String sector;

    
    @JsonAlias({ "type", "assetType" })
    private String type;
}
