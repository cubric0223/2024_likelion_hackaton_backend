package com.likelion.byoun_thinking.repository;

import com.likelion.byoun_thinking.entity.Challenge;
import com.likelion.byoun_thinking.entity.User;
import com.likelion.byoun_thinking.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChallengeRepository extends JpaRepository<UserChallenge,Integer> {
    UserChallenge findByUserAndChallenge(User user, Challenge challenge);
}
