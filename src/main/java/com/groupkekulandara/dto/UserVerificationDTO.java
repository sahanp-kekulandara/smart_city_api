package com.groupkekulandara.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVerificationDTO {

    public UserVerificationDTO(String verificationCode, Timestamp verificationCodeExpire, UserStatusDTO userStatus, UserDTO user) {
        this.verificationCode = verificationCode;
        this.verificationCodeExpire = verificationCodeExpire;
        this.user = user;
    }

    private int id;
    private String verificationCode;
    private Timestamp verificationCodeExpire;
    private UserDTO user;
}
