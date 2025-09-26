package com.example.InvestPlus.controllers;

import com.example.InvestPlus.responses.BrapiListResponse;
import com.example.InvestPlus.dtos.StockDto;
import com.example.InvestPlus.models.Observado;
import com.example.InvestPlus.repositories.ObservadoRepository;
import com.example.InvestPlus.services.BrapiService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/investimentos")
public class HomeController {

    private final BrapiService brapi;
    private final ObservadoRepository repo;

    public HomeController(BrapiService brapi, ObservadoRepository repo) {
        this.brapi = brapi;
        this.repo = repo;
    }

    @GetMapping({"", "/{categoria}"})
    public String index(@PathVariable(required = false) String categoria,
                        @RequestParam(defaultValue = "1") int page,
                        Model model) {

        int limit = 10;
        String tipo;

        // default
        String cat = categoria != null ? categoria.toLowerCase() : "acoes";

        switch (cat) {
            case "acoes" -> tipo = "stock";
            case "fiis" -> tipo = "fund";
            case "internacional" -> tipo = "bdr"; // se quiser: "etf" ou "bdr"
            case "favoritos" -> tipo = null; // sinaliza que vamos usar o DB
            default -> {
                cat = "acoes";
                tipo = "stock";
            }
        }

        List<StockDto> ativos;
        int currentPage = page;
        int totalPages = 1;

        if ("favoritos".equals(cat)) {
            // lê do DB e mapeia para StockDto (uniformiza a view)
            List<Observado> favoritos = repo.findAll();
            ativos = favoritos.stream().map(o -> {
                StockDto s = new StockDto();
                s.setStock(o.getSymbol());
                s.setName(o.getName());
                s.setType(o.getCategory()); // preenche type com a categoria salva
                // close/change/volume/logo/sector ficam nulos (não temos esses dados)
                return s;
            }).collect(Collectors.toList());

            currentPage = 1;
            totalPages = 1;
        } else {
            // chama a API brapi
            BrapiListResponse data = brapi.listarAtivos(tipo, page, limit);
            ativos = (data != null && data.getStocks() != null) ? data.getStocks() : Collections.emptyList();
            currentPage = (data != null) ? data.getCurrentPage() : page;
            totalPages = (data != null) ? data.getTotalPages() : 1;
        }

        // lista de observados (para checar se o ativo está favoritado)
        Set<String> observadoSymbols = repo.findAll().stream()
                .map(Observado::getSymbol)
                .collect(Collectors.toSet());

        model.addAttribute("ativos", ativos);
        model.addAttribute("categoria", cat);
        model.addAttribute("page", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("observados", repo.findAll()); // se precisar do objeto completo
        model.addAttribute("observadoSymbols", observadoSymbols); // set para checagem rápida

        return "index";
    }

    @PostMapping("/observados/toggle")
    @ResponseBody
    public Map<String, Object> toggle(@RequestBody Map<String, String> body) {
        String symbol = body.get("symbol");
        String name = body.get("name");
        String category = body.get("category");

        Optional<Observado> existing = repo.findBySymbol(symbol);
        Map<String, Object> result = new HashMap<>();
        if (existing.isPresent()) {
            repo.delete(existing.get());
            result.put("action", "removed");
        } else {
            Observado o = new Observado();
            o.setSymbol(symbol);
            o.setName(name);
            o.setCategory(category);
            o.setAddedAt(Instant.now());
            repo.save(o);
            result.put("action", "added");
        }
        result.put("symbol", symbol);
        return result;
    }
}



