package com.likelion.byoun_thinking.service;

import com.likelion.byoun_thinking.dto.MonthlyChalRankingResponseDTO;
import com.likelion.byoun_thinking.dto.MonthlyChallengeResponseDTO;
import com.likelion.byoun_thinking.dto.SchoolRankingDTO;
import com.likelion.byoun_thinking.entity.Challenge;
import com.likelion.byoun_thinking.entity.MonthlyChallenge;
import com.likelion.byoun_thinking.repository.CommentRepository;
import com.likelion.byoun_thinking.repository.MonthlyChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonthlyChallengeService {

    private final MonthlyChallengeRepository monthlyChallengeRepository;
    private final CommentRepository commentRepository;

    // 이달의 대항전 정보 조회 함수
    @Transactional
    public MonthlyChallengeResponseDTO getMonthlyChallenge(){
        MonthlyChallengeResponseDTO responseDTO = new MonthlyChallengeResponseDTO();

        MonthlyChallenge monthlyChallenge = findMonthlyChallenge();
        if(monthlyChallenge == null){
            return null;
        }

        Challenge challenge = monthlyChallenge.getChallenge();

        responseDTO.setChal_id(challenge.getChallengeId());
        responseDTO.setTitle(challenge.getTitle());
        responseDTO.setParticipants(challenge.getParticipants());
        return responseDTO;
    }

    // 이달의 대항전 순위 조회 함수
    public MonthlyChalRankingResponseDTO getMonthlyChalRanking(){
        MonthlyChallenge monthlyChallenge = findMonthlyChallenge();

        if(monthlyChallenge == null){
            return null;
        }

        MonthlyChalRankingResponseDTO responseDTO = new MonthlyChalRankingResponseDTO();
        List<SchoolRankingDTO> schoolRanking = commentRepository.getMonthlyChallengeSchoolRanking(monthlyChallenge.getChallenge().getChallengeId());
        responseDTO.setRank(schoolRanking.stream().limit(3).toList());
        return responseDTO;
    }

    // 이달의 대항전 총 순위 조회 함수
    public MonthlyChalRankingResponseDTO getMonthlyChalTotalRanking(){
        MonthlyChalRankingResponseDTO responseDTO = new MonthlyChalRankingResponseDTO();
        List<SchoolRankingDTO> schoolRanking = commentRepository.getMonthlyChallengeSchoolTotalRanking();
        responseDTO.setRank(schoolRanking.stream().toList());
        return responseDTO;
    }


    // 이달의 대항전 조회 함수
    public MonthlyChallenge findMonthlyChallenge(){
        List<MonthlyChallenge> monthlyChallenges = monthlyChallengeRepository.findAll();
        if(monthlyChallenges.isEmpty()){
            return null;
        }

        Date today = new Date();
        for(int i = monthlyChallenges.size() - 1; i >= 0; i--){
            Challenge challenge = monthlyChallenges.get(i).getChallenge();
            Date ch_start = challenge.getChStart();
            Date ch_end = challenge.getChEnd();
            if(ch_start.before(today) && ch_end.after(today)){
                return monthlyChallenges.get(i);
            }
        }
        return null;
    }
}
