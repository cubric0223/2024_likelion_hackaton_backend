package com.likelion.byoun_thinking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "monthly_challenge")
@Getter
@Setter
public class MonthlyChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_challenge_id", updatable = false)
    private Integer monthlyChallengeId;

    @OneToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;
}
