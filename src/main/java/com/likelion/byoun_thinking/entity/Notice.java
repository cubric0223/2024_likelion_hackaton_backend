package com.likelion.byoun_thinking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "Notice")
@Getter
@Setter
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticeId;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;

    @Column(name = "important", nullable = false)
    private Boolean important;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;
}
