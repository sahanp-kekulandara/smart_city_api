package com.groupkekulandara.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class OrderStatusDTO {
    private Integer id;

    private String status;
}
