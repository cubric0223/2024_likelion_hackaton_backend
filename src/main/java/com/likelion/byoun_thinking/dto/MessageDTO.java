package com.likelion.byoun_thinking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {
    private String message;
    private String sessionId;

    public MessageDTO(String message){
        this.message = message;
    }
}
