package com.example.CMCmp3.controller;

import com.example.CMCmp3.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartController {
    private final ChartService chartService;

    @GetMapping("/realtime")
    public Map<String, Object> realtime() {
        return chartService.getRealtime();
    }

    @GetMapping("/weekly")
    public List<Map<String, String>> weekly() {
        return chartService.getWeeklyTiles();
    }
}
