package com.groupkekulandara.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorDTO {
    private long id;
    private String businessName;
    private String description;
    private double latitude;
    private double longitude;
    private short isVerified;
    private String phoneNumber;
}
