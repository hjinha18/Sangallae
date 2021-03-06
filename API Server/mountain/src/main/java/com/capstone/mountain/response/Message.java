package com.capstone.mountain.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Message {

    private HttpStatus status;
    private String message;
    private Object data;

    public Message() {
        this.status = HttpStatus.FORBIDDEN;
        this.data = null;
        this.message = "로그인이 필요한 서비스입니다.";
    }
}