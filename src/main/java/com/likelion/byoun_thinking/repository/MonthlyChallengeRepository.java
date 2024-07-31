package com.likelion.byoun_thinking.repository;

import com.likelion.byoun_thinking.entity.MonthlyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyChallengeRepository extends JpaRepository<MonthlyChallenge, Integer> {
}
