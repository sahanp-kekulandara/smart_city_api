package com.groupkekulandara.services;

import com.groupkekulandara.dto.CategoryDTO;
import com.groupkekulandara.models.Category;
import com.groupkekulandara.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryService {

    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<CategoryDTO> getAllCategories() {
        // 1. Fetch the raw Entities from the Repository
        List<Category> categories = categoryRepository.allCategories();

        // 2. Convert Entities to DTOs for the Android App
        // This ensures we don't send extra database info to the mobile app
        return categories.stream().map(category -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setImageUrl(category.getImageUrl());
            return dto;
        }).collect(Collectors.toList());
    }
}
