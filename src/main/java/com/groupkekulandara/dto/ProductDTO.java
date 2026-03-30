package com.groupkekulandara.dto;

import com.groupkekulandara.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private long id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private String imageUrl;
    private VendorDTO vendor;
    private CategoryDTO category_id;
    private ProductStatusDTO product_status;
}