package com.likelion.byoun_thinking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Date;
import java.util.ArrayList;

import java.util.List;

@Entity
@Table(name = "challenge")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_Id", nullable = false)
    private Integer challengeId;

    @OneToMany(mappedBy = "challenge")
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge")
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "challenge")
    private MonthlyChallenge monthlyChallenge;

    @ManyToOne
    @JoinColumn(name = "school_Id")
    private School school;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "introduce", nullable = false)
    private String introduce;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "authentication", nullable = false)
    private Boolean authentication;

    @Column(name = "participants", nullable = false)
    @ColumnDefault("0")
    private Integer participants;

    @Column(name = "ch_Start", nullable = false)
    private Date chStart;

    @Column(name = "ch_End", nullable = false)
    private Date chEnd;
}
