package com.groupkekulandara.dto;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class OrderShippingDTO {

    private Long id;
    private double latitude;
    private double longitude;
    private String addressName;
    private String formattedAddress;
}


