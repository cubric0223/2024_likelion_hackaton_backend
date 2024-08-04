package com.likelion.byoun_thinking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Integer id;
    private String name;
    private String email;
    private String schoolName;
    private String imageUrl;

    public UserInfoDTO(String name, String email, String schoolName, String imageUrl) {
        this.name = name;
        this.email = email;
        this.schoolName = schoolName;
        this.imageUrl = imageUrl;
    }
}
