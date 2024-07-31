package com.likelion.byoun_thinking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDetailCommentDTO {
    private String name;
    private String content;
    private String image_url;
}
