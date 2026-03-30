package com.groupkekulandara.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "vendor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorProfile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "is_verified")
    private short isVerified;

    @Column(name = "phone_number")
    private String phoneNumber;
}