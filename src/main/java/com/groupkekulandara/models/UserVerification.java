package com.groupkekulandara.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "user_verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVerification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "verification_code", length = 45)
    private String verificationCode;

    @Column(name = "verification_code_expire")
    private Timestamp verificationCodeExpire;

    @OneToOne
    @JoinColumn(name = "users_id")
    private User user;
}