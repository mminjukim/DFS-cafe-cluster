package com.algorithm.project.controller;

import com.algorithm.project.domain.CoffeeShop;
import com.algorithm.project.service.ForestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ForestController {

    private final ForestService forestService;

    @GetMapping("/")
    public String viewForests(@RequestParam(name = "distance", defaultValue = "50") double distance,
                              Model model
    ) {
        // 사용자가 선택한 거리로 군집 분석 수행
        List<List<CoffeeShop>> clusters = forestService.analyzeClusters(distance);

        model.addAttribute("clusters", clusters);
        model.addAttribute("totalClusters", clusters.size());
        model.addAttribute("currentDistance", distance);

        return "forest-view";
    }
}
