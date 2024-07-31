package com.likelion.byoun_thinking.repository;

import com.likelion.byoun_thinking.dto.SchoolRankingDTO;
import com.likelion.byoun_thinking.entity.Challenge;
import com.likelion.byoun_thinking.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByChallenge(Challenge challenge);

    @Query("SELECT c FROM Comment c WHERE c.challenge.challengeId = :challengeId " +
            "AND c.user.userId = :userId AND DATE(c.time) = current_date ")
    Comment findCommentsByChallengeIdAndUserIdAndDate(@Param("challengeId") Integer challengeId,
                                                            @Param("userId") Integer userId);

    @Query("select " +
            "new com.likelion.byoun_thinking.dto.SchoolRankingDTO(s.name, COUNT(c))" +
            "from Comment c " +
            "left join User u on c.user.userId = u.userId " +
            "left join School s on s.schoolId = u.school.schoolId " +
            "where c.challenge.challengeId = :challengeId " +
            "group by s.schoolId " +
            "order by COUNT(c) desc")
    List<SchoolRankingDTO> getMonthlyChallengeSchoolRanking(@Param("challengeId") Integer challengeId);

    @Query("select " +
            "new com.likelion.byoun_thinking.dto.SchoolRankingDTO(s.name, COUNT(c)) " +
            "from Comment c " +
            "left join User u on c.user.userId = u.userId " +
            "left join School s on s.schoolId = u.school.schoolId " +
            "left join Challenge ch on ch.challengeId = c.challenge.challengeId " +
            "left join MonthlyChallenge mc on mc.challenge.challengeId = ch.challengeId " +
            "group by s.schoolId " +
            "order by COUNT(c) desc")
    List<SchoolRankingDTO> getMonthlyChallengeSchoolTotalRanking();

}
