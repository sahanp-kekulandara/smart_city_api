package com.groupkekulandara.dto;

import com.groupkekulandara.models.OrderShipping;
import com.groupkekulandara.models.OrderStatus;
import com.groupkekulandara.models.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private User user;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private OrderShipping orderShipping;
    private OrderStatus orderStatus;
}