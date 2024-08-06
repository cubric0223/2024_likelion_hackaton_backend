package com.likelion.byoun_thinking.service;

import com.likelion.byoun_thinking.dto.*;
import com.likelion.byoun_thinking.entity.*;
import com.likelion.byoun_thinking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final CommentRepository commentRepository;
    private final SchoolRepository schoolRepository;

    // 검색 함수
    public List<Challenge> searchChallengesByKeyword(String keyword) {
        return challengeRepository.findByKeyword(keyword);
    }

    // 인기 급상승 전체 챌린지 함수
    public List<Challenge> getTrendingChallenges() {
        // 전체 챌린지 중에서
        return challengeRepository.findTop3BySchoolIdOrderByParticipantsDesc(1);
    }

    // 학교 Id로 챌린지 찾는 함수 => 전체 챌린지 교내 챌린지 둘 다 가능
    public List<ChallengeMainInfoDTO> getChallenges(Integer userId) {
        return challengeRepository.findByUserId(userId);
    }

    // 참여중인 교내 챌린지 찾는 함수
    public List<String> getMySchoolChallenges(Integer userId) {
        return challengeRepository.findTop6SchoolChallengesByUserId(userId);
    }

    public List<String> getMyAllChallenges(Integer userId) {
        return challengeRepository.findTop6AllChallengesByUserId(userId);
    }

    // 이달의 챌린지 찾는 함수
    public List<Challenge> getMonthlyChallenges() {
        return challengeRepository.findMonthlyChallenges();
    }

    // 이달의 챌린지에서 상위 3위 학교 찾는 함수
    public List<String> getMonthlyChallengesRanking() {
        return challengeRepository.findMonthlyChallengeRanking();
    }

    // 챌린지 참여 함수
    @Transactional
    public void joinChallenge(Integer chal_id, Integer user_id) {
        User user = userRepository.findById(user_id).orElseThrow(
                () -> new IllegalArgumentException("not found: " + user_id)
        );
        Challenge challenge = challengeRepository.findById(chal_id).orElseThrow(
                () -> new IllegalArgumentException("not found: " + chal_id)
        );

        if(checkJoined(user, challenge)){ // 이미 참여했는지 확인
            return;
        }

        if(!checkChallengeAndSchool(challenge, user)){ // 챌린지의 학교 정보와 학생의 학교 정보를 확인
            return;
        }

        userChallengeRepository.save(UserChallenge.builder()
                .user(user)
                .challenge(challenge)
                .build()
        );

        challenge.setParticipants(challenge.getParticipants()+1);
        challengeRepository.save(challenge);

        return;
    }

    // 챌린지 상세 조회 함수
    @Transactional
    public ChallengeDetailDTO challengeDetail(Integer user_id, Integer chal_id) {
        ChallengeDetailDTO challengeDetailDTO = new ChallengeDetailDTO();
        List<ChallengeDetailCommentDTO> userComments = new ArrayList<>();

        User user = userRepository.findById(user_id).orElseThrow(
                ()-> new IllegalArgumentException("not found: " + user_id)
        );

        Challenge challenge = challengeRepository.findById(chal_id).orElseThrow(
                () -> new IllegalArgumentException("not found: " + chal_id)
        );

        List<Comment> comments = commentRepository.findByChallenge(challenge);

        Boolean isAlreadyJoined = checkJoined(user, challenge );

        boolean isCertifyToday = checkAlreadyAuth(user,challenge);

        challengeDetailDTO.setTitle(challenge.getTitle());
        challengeDetailDTO.setIntroduce(challenge.getIntroduce());
        challengeDetailDTO.setDescription(challenge.getDescription());
        challengeDetailDTO.setAuthentication(challenge.getAuthentication());
        challengeDetailDTO.setParticipants(challenge.getParticipants());
        challengeDetailDTO.setChStart(challenge.getChStart());
        challengeDetailDTO.setChEnd(challenge.getChEnd());
        challengeDetailDTO.setIsParticipate(isAlreadyJoined);
        challengeDetailDTO.setIsCertifyToday(isCertifyToday);

        for(Comment c : comments){
            ChallengeDetailCommentDTO commentDTO = new ChallengeDetailCommentDTO();
            commentDTO.setContent(c.getContent());
            commentDTO.setName(c.getUser().getName());
            commentDTO.setImage_url(c.getImageUrl());
            userComments.add(commentDTO);
        }
        challengeDetailDTO.setUserComments(userComments);

        return challengeDetailDTO;
    }

    // 챌린지 인증(사진 버전)
    public boolean authChallengeWithImage(ChallengeImageAuthRequestDTO requestDTO, Integer user_id, Integer chal_id){
        Challenge challenge = challengeRepository.findById(chal_id).orElseThrow(
                () -> new IllegalArgumentException("not found: " + chal_id)
        );

        User user = userRepository.findById(user_id).orElseThrow(
                () -> new IllegalArgumentException("not found: " + user_id)
        );

        // 챌린지에 참가 신청했는지, 오늘 이미 인증했는지 확인
        if(!checkJoined(user, challenge) || checkAlreadyAuth(user, challenge)){
            return false;
        }

        Comment comment = new Comment();
        comment.setChallenge(challenge);
        comment.setUser(user);
        comment.setContent(requestDTO.getContent());
        comment.setImageUrl(requestDTO.getImage_Url());
        comment.setTime(LocalDateTime.now());
        commentRepository.save(comment);

        return true;
    }

    // 챌린지 인증(텍스트 버전)
    public boolean authChallengeWithText(ChallengeTextAuthRequestDTO requestDTO, Integer user_id, Integer chal_id){
        Challenge challenge = challengeRepository.findById(chal_id).orElseThrow(
                () -> new IllegalArgumentException("not found: " + chal_id)
        );

        User user = userRepository.findById(user_id).orElseThrow(
                () -> new IllegalArgumentException("not found: " + user_id)
        );

        // 챌린지에 참가 신청했는지, 오늘 이미 인증했는지 확인
        if(!checkJoined(user, challenge) || checkAlreadyAuth(user, challenge)){
            return false;
        }

        Comment comment = new Comment();
        comment.setChallenge(challenge);
        comment.setUser(user);
        comment.setContent(requestDTO.getContent());
        comment.setTime(LocalDateTime.now());
        commentRepository.save(comment);

        return true;
    }

    // 챌린지 생성 함수
    public void createChallenge(CreateChallengeRequestDTO requestDTO, Integer user_id){

        School school;

        if(requestDTO.getOpen()){ // 전체 챌린지
            school = schoolRepository.findById(1).get();
        }else{ // 교내 챌린지
            User user = userRepository.findById(user_id).orElseThrow(
                    () -> new IllegalArgumentException("not found: " + user_id)
            );
            school = user.getSchool();
        }

        challengeRepository.save(Challenge.builder()
                .title(requestDTO.getTitle())
                .introduce(requestDTO.getIntroduce())
                .description(requestDTO.getDescription())
                .authentication(requestDTO.getAuthentication())
                .chStart(requestDTO.getCh_start())
                .chEnd(requestDTO.getCh_end())
                .school(school)
                .build());
    }

    // 학생의 챌린지 참가를 확인하는 함수
    public boolean checkJoined(User user, Challenge challenge) {
        return userChallengeRepository.findByUserAndChallenge(user, challenge) != null;
    }

    // 학생의 학교 번호와 챌린지의 학교번호를 확인하는 함수
    public boolean checkChallengeAndSchool(Challenge challenge,User user) {
        Integer schoolIdOfChallenge = challenge.getSchool().getSchoolId();
        Integer schoolIdOfUser = user.getSchool().getSchoolId();
        return Objects.equals(schoolIdOfChallenge, schoolIdOfUser) || schoolIdOfChallenge == 1;
    }

    // 오늘 챌린지 인증했는지 확인하는 함수
    public boolean checkAlreadyAuth(User user, Challenge challenge) {
        return commentRepository.findCommentsByChallengeIdAndUserIdAndDate(challenge.getChallengeId(),user.getUserId()) != null;
    }

    // 내 정보에서 참가 중인 교내 챌린지 찾는 함수
    public List<ChallengeInfoDTO> getMySchoolChallengeInfo(Integer userId) {
        return challengeRepository.findMySchoolChallengeInfo(userId);
    }

    // 내 정보에서 참가 중인 전체 챌린지 찾는 함수
    public List<ChallengeInfoDTO> getMyAllChallengeInfo(Integer userId) {
        return challengeRepository.findMyAllChallengeInfo(userId);
    }

}
