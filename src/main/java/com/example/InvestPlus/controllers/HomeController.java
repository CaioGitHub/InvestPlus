package com.example.InvestPlus.controllers;

import com.example.InvestPlus.responses.BrapiListResponse;
import com.example.InvestPlus.dtos.StockDto;
import com.example.InvestPlus.models.Observado;
import com.example.InvestPlus.repositories.ObservadoRepository;
import com.example.InvestPlus.services.BrapiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
                        @RequestParam(defaultValue = "name") String sort,
                        @RequestParam(defaultValue = "asc") String dir,
                        @RequestParam(required = false) String codigo,
                        Model model) {

        int limit = 10;
        String tipo;
        String cat = categoria != null ? categoria.toLowerCase() : "acoes";

        switch (cat) {
            case "acoes" -> tipo = "stock";
            case "fiis" -> tipo = "fund";
            case "favoritos" -> tipo = null;
            default -> {
                cat = "acoes";
                tipo = "stock";
            }
        }

        List<StockDto> ativos;
        int currentPage = page;
        int totalPages = 1;

        if (codigo != null && !codigo.trim().isEmpty()) {
            StockDto ativo = brapi.buscarPorCodigo(codigo.trim().toUpperCase());
            ativos = (ativo != null) ? List.of(ativo) : Collections.emptyList();
            currentPage = 1;
            totalPages = 1;

        } else if ("favoritos".equals(cat)) {
            Page<Observado> pageFav = repo.findAll(
                    PageRequest.of(Math.max(page - 1, 0), limit,
                            "desc".equalsIgnoreCase(dir) ? Sort.by(sort).descending() : Sort.by(sort).ascending()
                    )
            );

            ativos = pageFav.stream().map(o -> {
                StockDto s = new StockDto();
                s.setStock(o.getSymbol());
                s.setName(o.getName());
                s.setType(o.getCategory());
                return s;
            }).toList();

            currentPage = pageFav.getNumber() + 1;
            totalPages = pageFav.getTotalPages() == 0 ? 1 : pageFav.getTotalPages();

        } else {
            BrapiListResponse data = brapi.listarAtivos(tipo, page, limit, sort, dir);
            ativos = brapi.listarAtivosDto(tipo, page, limit, sort, dir);
            currentPage = (data != null) ? data.getCurrentPage() : page;
            totalPages = (data != null) ? Math.max(1, data.getTotalPages()) : 1;
        }

        Set<String> observadoSymbols = repo.findAll().stream()
                .map(Observado::getSymbol)
                .collect(Collectors.toSet());

        model.addAttribute("ativos", ativos);
        model.addAttribute("categoria", cat);
        model.addAttribute("page", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("observados", repo.findAll());
        model.addAttribute("observadoSymbols", observadoSymbols);

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