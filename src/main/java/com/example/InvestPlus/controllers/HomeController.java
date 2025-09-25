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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/investimentos")
public class HomeController {

    private final BrapiService brapi;
    private final ObservadoRepository repo;
    private final ObjectMapper mapper;

    public HomeController(BrapiService brapi, ObservadoRepository repo, ObjectMapper mapper) {
        this.brapi = brapi;
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping({"", "/{categoria}"})
    public String index(@PathVariable(required = false) String categoria,
                        @RequestParam(defaultValue = "1") int page,
                        Model model) {
        int limit = 10;
        String tipo;

        switch (categoria != null ? categoria.toLowerCase() : "acoes") {
            case "acoes" -> tipo = "stock";
            case "fiis" -> tipo = "fund";
            case "internacional" -> tipo = "bdr";
            default -> {
                categoria = "acoes";
                tipo = "stock";
            }
        }

        BrapiListResponse data = brapi.listarAtivos(tipo, page, limit);
        List<StockDto> ativos = data != null && data.getStocks() != null ? data.getStocks() : Collections.emptyList();

        model.addAttribute("ativos", ativos);
        model.addAttribute("categoria", categoria);
        model.addAttribute("page", data != null ? data.getCurrentPage() : page);
        model.addAttribute("totalPages", data != null ? data.getTotalPages() : 1);
        model.addAttribute("observados", repo.findAll());
        return "index";
    }


    @PostMapping("/observados/toggle")
    @ResponseBody
    public ResponseEntity<?> toggle(@RequestBody Map<String, String> body) {
        String symbol = body.get("symbol");
        String name = body.get("name");
        String category = body.get("category");

        repo.findBySymbol(symbol).ifPresentOrElse(existing -> {
            repo.delete(existing);
        }, () -> {
            Observado o = new Observado();
            o.setSymbol(symbol);
            o.setName(name);
            o.setCategory(category);
            o.setAddedAt(Instant.now());
            repo.save(o);
        });
        return ResponseEntity.ok().build();
    }
}



