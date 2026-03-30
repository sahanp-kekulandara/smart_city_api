package com.groupkekulandara.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Useful for creating new users in your Registration logic
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @ToString.Exclude
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_pic_url")
    private String profilePicUrl;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "user_status_id")
    private UserStatus userStatus;

    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;
}