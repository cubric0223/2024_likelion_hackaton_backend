package com.likelion.byoun_thinking.repository;

import com.likelion.byoun_thinking.dto.ChallengeInfoDTO;
import com.likelion.byoun_thinking.dto.ChallengeMainInfoDTO;
import com.likelion.byoun_thinking.entity.Challenge;
import com.likelion.byoun_thinking.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {
    @Query("SELECT c FROM Challenge c WHERE c.title " +
            "LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Challenge> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT new com.likelion.byoun_thinking.dto.ChallengeMainInfoDTO(c.challengeId, c.title, c.description, c.participants) " +
            "FROM Challenge c " +
            "JOIN User u ON c.school = u.school " +
            "WHERE u.userId = :userId " +
            "GROUP BY c.challengeId, c.title, c.description, c.participants")
    List<ChallengeMainInfoDTO> findByUserId(@Param("userId") int userId);

    @Query("SELECT c FROM Challenge c WHERE c.school.schoolId = :schoolId " +
            "ORDER BY c.participants DESC")
    List<Challenge> findTop3BySchoolIdOrderByParticipantsDesc(@Param("schoolId") int schoolId);

    // 참여중인 교내 챌린지
    @Query("SELECT c.title FROM UserChallenge uc " +
            "JOIN uc.challenge c " +
            "WHERE uc.user.userId = :userId AND c.challengeId <> 1")
    List<String> findTop6SchoolChallengesByUserId(@Param("userId") Integer userId);

    // 참여중인 전체 챌린지
    @Query("SELECT c.title FROM UserChallenge uc " +
            "JOIN uc.challenge c " +
            "WHERE uc.user.userId = :userId AND c.challengeId = 1")
    List<String> findTop6AllChallengesByUserId(@Param("userId") Integer userId);


    @Query("SELECT c FROM Challenge c JOIN MonthlyChallenge mc ON c.challengeId = mc.challenge.challengeId " +
            "WHERE CURRENT_DATE BETWEEN c.chStart AND c.chEnd")
    List<Challenge> findMonthlyChallenges();

    @Query("SELECT s.name FROM School s " +
            "JOIN User u ON s.schoolId = u.school.schoolId " +
            "JOIN UserChallenge uc ON u.userId = uc.user.userId " +
            "JOIN Challenge c ON uc.challenge.challengeId = c.challengeId " +
            "JOIN MonthlyChallenge mc ON c.challengeId = mc.challenge.challengeId " +
            "WHERE CURRENT_DATE BETWEEN c.chStart AND c.chEnd " +
            "GROUP BY s.name " +
            "ORDER BY COUNT(uc.user) DESC")
    List<String> findMonthlyChallengeRanking();

    @Query("SELECT new com.likelion.byoun_thinking.dto.ChallengeInfoDTO(c.challengeId, c.title, c.description, COUNT(cm.commentId)) " +
            "FROM Challenge c " +
            "JOIN Comment cm ON cm.challenge.challengeId = c.challengeId " +
            "JOIN User u ON u.userId = cm.user.userId " +
            "WHERE u.userId = :userId " +
            "GROUP BY c.challengeId, c.title, c.description " +
            "ORDER BY c.participants DESC")
    List<ChallengeInfoDTO> findMySchoolChallengeInfo(@Param("userId") Integer userId);

    @Query("SELECT new com.likelion.byoun_thinking.dto.ChallengeInfoDTO(c.challengeId, c.title, c.description, COUNT(cm.commentId)) " +
            "FROM Challenge c " +
            "JOIN Comment cm ON cm.challenge.challengeId = c.challengeId " +
            "JOIN User u ON u.userId = cm.user.userId " +
            "WHERE u.userId = :userId AND c.school.schoolId = 1 " +
            "GROUP BY c.challengeId, c.title, c.description " +
            "ORDER BY c.participants DESC")
    List<ChallengeInfoDTO> findMyAllChallengeInfo(@Param("userId") Integer userId);

//    @Query("SELECT c FROM Challenge c WHERE (c.schoolId = 0 OR c.schoolId = :schoolId) " +
//            "AND (c.title LIKE %:keyword% OR c.description LIKE %:keyword%) " +
//            "ORDER BY c.participants DESC")
//    List<Challenge> findTrendingByKeyword(@Param("keyword") String keyword, @Param("schoolId") int schoolId);

}
