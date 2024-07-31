package com.likelion.byoun_thinking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDTO {
    private String title;
    private String content;
    private Boolean important;
}
