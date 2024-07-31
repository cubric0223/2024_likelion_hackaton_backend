package com.likelion.byoun_thinking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDetailDTO {
    private String title;
    private String introduce;
    private String description;
    private Boolean authentication;
    private Integer participants;
    private Date chStart;
    private Date chEnd;
    private Boolean isParticipate;
    private Boolean isCertifyToday;
    private List<ChallengeDetailCommentDTO> userComments;
}
