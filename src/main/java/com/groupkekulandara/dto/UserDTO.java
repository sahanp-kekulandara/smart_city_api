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
public class UserDTO {

    public UserDTO(String email, String password){
        this.email = email;
        this.password = password;
    }

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String profilePicUrl;
    private int phoneNumber;
    private Timestamp createdAt;
    private UserRoleDTO userRole;
    private UserStatusDTO userStatusDTO;
}
