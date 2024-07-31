package com.likelion.byoun_thinking.controller;

import com.likelion.byoun_thinking.dto.MessageDTO;
import com.likelion.byoun_thinking.dto.MonthlyChalRankingResponseDTO;
import com.likelion.byoun_thinking.dto.MonthlyChallengeResponseDTO;
import com.likelion.byoun_thinking.dto.SchoolRankingDTO;
import com.likelion.byoun_thinking.service.MonthlyChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class MonthlyChallengeController {

    private final MonthlyChallengeService monthlyChallengeService;

    @GetMapping("/chal/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyChallenge() {
        Map<String, Object> result = new HashMap<>();
        MonthlyChallengeResponseDTO responseDTO = monthlyChallengeService.getMonthlyChallenge();
        if(responseDTO == null) {
            result.put("message", "이달의 챌린지가 없습니다.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
        result.put("monthlyChallenge", responseDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/chal/monthly/rank")
    public ResponseEntity<Map<String, Object>> getMonthlyChallengeRank() {
        Map<String, Object> result = new HashMap<>();
        MonthlyChalRankingResponseDTO responseDTO = monthlyChallengeService.getMonthlyChalRanking();

        if(responseDTO == null) {
            result.put("message", "이달의 챌린지가 없습니다.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
        result.put("rank", responseDTO.getRank());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/chal/monthly/totalRank")
    public ResponseEntity<Map<String, Object>> getMonthlyChallengeTotalRank() {
        Map<String, Object> result = new HashMap<>();
        MonthlyChalRankingResponseDTO responseDTO = monthlyChallengeService.getMonthlyChalTotalRanking();

        if(responseDTO == null) {
            result.put("message", "이달의 챌린지가 없습니다.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
        result.put("rank", responseDTO.getRank());
        return ResponseEntity.ok(result);
    }
}