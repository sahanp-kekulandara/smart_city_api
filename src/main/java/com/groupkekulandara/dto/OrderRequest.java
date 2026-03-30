package com.groupkekulandara.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private long userId;
    private List<CartItemDTO> items;
    private double totalAmount;
    private double latitude;
    private double longitude;
    private String address;
}