package com.groupkekulandara.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    public CartItemDTO(ProductDTO product,int quantity){
        this.productId = product;
        this.quantity = quantity;
    }

    private long id;
    private UserDTO userId;
    private ProductDTO productId;
    private int quantity;
}