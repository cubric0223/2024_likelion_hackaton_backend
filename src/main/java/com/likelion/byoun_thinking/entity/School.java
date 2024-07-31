package com.likelion.byoun_thinking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "school")
@Getter
@Setter
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_Id", nullable = false)
    private Integer schoolId;

    @OneToMany(mappedBy = "school")
    private List<User> user = new ArrayList<>();

    @OneToMany(mappedBy = "school")
    private List<Challenge> challenges = new ArrayList<>();

    @Column(name = "name", nullable = false)
    private String name;
}
