package com.likelion.byoun_thinking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticePostDTO {
    private Integer userId;
    private String title;
    private String content;
    private Boolean important;
    private Date modifiedAt;
}