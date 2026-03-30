package com.groupkekulandara.dto;

import com.groupkekulandara.models.Order;
import com.groupkekulandara.models.Product;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class OrderItemDTO{

    private Long id;

    private Order order;

    private Product product;

    private Integer quantity;

    private Double priceAtPurchase;
}
