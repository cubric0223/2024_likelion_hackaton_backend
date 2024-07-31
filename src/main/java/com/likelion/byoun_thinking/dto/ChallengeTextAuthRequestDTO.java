package com.likelion.byoun_thinking.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeTextAuthRequestDTO {

    @NotEmpty(message = "내용을 작성해주세요.")
    private String content;
}
