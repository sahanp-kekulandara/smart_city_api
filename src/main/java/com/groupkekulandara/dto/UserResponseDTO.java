package com.groupkekulandara.dto;

import com.groupkekulandara.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    public UserResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public UserResponseDTO(boolean success, String message, Long userId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
    }

    private boolean success;
    private String message;
    private Long userId;
    private User user;
}