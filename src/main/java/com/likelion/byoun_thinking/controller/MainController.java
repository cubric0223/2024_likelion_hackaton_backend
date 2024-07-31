package com.likelion.byoun_thinking.controller;

import com.likelion.byoun_thinking.dto.ChallengeSearchDTO;
import com.likelion.byoun_thinking.dto.SchoolNameTrendDTO;
import com.likelion.byoun_thinking.entity.Challenge;
import com.likelion.byoun_thinking.entity.School;
import com.likelion.byoun_thinking.service.ChallengeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController()
@RequiredArgsConstructor
public class MainController {
    private final ChallengeService challengeService;

    private Map<String, List<ChallengeSearchDTO>> getStringListMap(List<Challenge> challenges) {
        List<ChallengeSearchDTO> collect = challenges.stream()
                .map(challenge -> new ChallengeSearchDTO(challenge.getTitle()))
                .collect(Collectors.toList());

        Map<String, List<ChallengeSearchDTO>> response = new HashMap<>();
        response.put("results", collect);
        return response;
    }

    @GetMapping("/main/search")
    public Map<String, List<ChallengeSearchDTO>> getChallenges(@RequestParam String keyword) {
        //검색
        List<Challenge> challenges = challengeService.searchChallengesByKeyword(keyword);
        return getStringListMap(challenges);
    }

    // TODO 인기 급상승 기준 정하기
    @GetMapping("/main/trendChal")
    public Map<String, List<ChallengeSearchDTO>> getTrendingChallenges() {
        List<Challenge> challenges = challengeService.getTrendingChallenges();

        return getStringListMap(challenges);
    }

    @GetMapping("/main/schoolChal")
    public Map<String, List<ChallengeSearchDTO>> getSchoolChallenges(HttpServletRequest httpServletRequest) {
        // 가져온 세션에서 userId 없으면 에러
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not login or session expired :  getSchoolChallenges method");
        }
        Integer userId = (Integer) session.getAttribute("userId");
        List<Challenge> challenges = challengeService.getChallenges(userId);

        return getStringListMap(challenges);
    }

    @GetMapping("/main/allChal")
    public Map<String, List<ChallengeSearchDTO>> getSchoolChallenges() {
        // 전체 챌린지 중에서 userId == 1 관리자
        List<Challenge> challenges = challengeService.getChallenges(1);
        return getStringListMap(challenges);
    }

    // 참여중인 교내 챌린지
    @GetMapping("/main/mySchoolChal")
    public Map<String, List<ChallengeSearchDTO>> getMySchoolChallenges(HttpServletRequest httpServletRequest) {
        // 가져온 세션에서 userId 없으면 에러
        HttpSession session = httpServletRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not login or session expired : getMyChallenges method");
        }
        Integer userId = (Integer) session.getAttribute("userId");
        List<String> challenges = challengeService.getMySchoolChallenges(userId);

        List<ChallengeSearchDTO> challengeSearchDTOList = challenges.stream()
                .map(ChallengeSearchDTO::new) // 각 String을 ChallengeSearchDTO 객체로 변환
                .collect(Collectors.toList());

        // Map 생성 및 데이터 삽입
        Map<String, List<ChallengeSearchDTO>> response = new HashMap<>();
        response.put("results", challengeSearchDTOList);

        return response;
    }

    // 참여중인 전체 챌린지
    @GetMapping("/main/myAllChal")
    public Map<String, List<ChallengeSearchDTO>> getMyAllChallenges(HttpServletRequest httpServletRequest) {
        // 가져온 세션에서 userId 없으면 에러
        HttpSession session = httpServletRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not login or session expired : getMyChallenges method");
        }
        Integer userId = (Integer) session.getAttribute("userId");
        List<String> challenges = challengeService.getMyAllChallenges(userId);

        List<ChallengeSearchDTO> challengeSearchDTOList = challenges.stream()
                .map(ChallengeSearchDTO::new) // 각 String을 ChallengeSearchDTO 객체로 변환
                .collect(Collectors.toList());

        // Map 생성 및 데이터 삽입
        Map<String, List<ChallengeSearchDTO>> response = new HashMap<>();
        response.put("results", challengeSearchDTOList);

        return response;
    }

    @GetMapping("/main/monthlyChal")
    public Map<String, List<ChallengeSearchDTO>> getMonthlyChallenges() {
        List<Challenge> challenges = challengeService.getMonthlyChallenges();
        return getStringListMap(challenges);
    }

    @GetMapping("/main/monthlyChal/ranking")
    public Map<String, List<SchoolNameTrendDTO>> getMonthlyChallengesRanking() {
        List<String> challenges = challengeService.getMonthlyChallengesRanking();

        List<SchoolNameTrendDTO> collect = challenges.stream()
                .map(SchoolNameTrendDTO::new)
                .collect(Collectors.toList());

        Map<String, List<SchoolNameTrendDTO>> response = new HashMap<>();
        response.put("results", collect);
        return response;
    }
}
