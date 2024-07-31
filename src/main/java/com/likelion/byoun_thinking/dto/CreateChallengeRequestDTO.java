package com.likelion.byoun_thinking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.sql.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChallengeRequestDTO {

    private Boolean open;

    @NotBlank(message = "제목을 작성해주세요.")
    private String title;

    @NotBlank(message = "소개를 작성해주세요.")
    private String introduce;

    @NotBlank(message = "방법을 작성해주세요.")
    private String description;

    private Boolean authentication;

    private Date ch_start;

    private Date ch_end;
}
