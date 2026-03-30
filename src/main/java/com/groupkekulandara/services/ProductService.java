package com.groupkekulandara.services;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.dto.CategoryDTO;
import com.groupkekulandara.dto.ProductDTO;
import com.groupkekulandara.dto.VendorDTO;
import com.groupkekulandara.models.Category;
import com.groupkekulandara.models.Product;
import com.groupkekulandara.models.ProductStatus;
import com.groupkekulandara.models.VendorProfile;
import com.groupkekulandara.repository.ProductRepository;
import com.groupkekulandara.repository.VendorRepository;
import jakarta.persistence.EntityManager;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductRepository productRepository = new ProductRepository();

    public List<ProductDTO> getProducts(String name) {
        List<Product> products;

        // Check if the name is provided for searching
        if (name != null && !name.trim().isEmpty()) {
            // Use the search repository method
            products = productRepository.searchByName(name.trim());
        } else {
            // Use the default 'get all' method
            products = productRepository.allProducts();
        }

        return mapEntitiesToDTO(products);
    }

    public List<ProductDTO> getNewArrivals() {
        List<Product> products = productRepository.getNewArrivals();
        return mapEntitiesToDTO(products);
    }

    public List<ProductDTO> mapEntitiesToDTO(List<Product> products) {
        return products.stream().map(p -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setStockQuantity(p.getStockQuantity());
            dto.setImageUrl(p.getImageUrl());

            // 1. Map Category Entity to CategoryDTO
            if (p.getCategory() != null) {
                CategoryDTO catDto = new CategoryDTO();
                catDto.setId(p.getCategory().getId());
                catDto.setName(p.getCategory().getName());
                dto.setCategory_id(catDto);
            }

            // 2. Map Vendor Entity to VendorDTO
            if (p.getVendor() != null) {
                VendorDTO venDto = new VendorDTO();
                venDto.setId(p.getVendor().getId());
                venDto.setBusinessName(p.getVendor().getBusinessName());
                venDto.setLatitude(p.getVendor().getLatitude());
                venDto.setLongitude(p.getVendor().getLongitude());
                dto.setVendor(venDto);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private static final String PROJECT_ROOT = "C:\\Users\\ADMIN\\IdeaProjects\\AndroidProjectAPI";
    private static final String UPLOAD_DIR = PROJECT_ROOT + File.separator +
            "src" + File.separator +
            "main" + File.separator +
            "webapp" + File.separator +
            "uploads" + File.separator +
            "products" + File.separator;

    public Product saveProduct(long uid,Product product, int categoryId, InputStream imageStream, FormDataContentDisposition meta) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        ProductRepository productRepo = new ProductRepository();
        // Find Category
        Category category = em.find(Category.class,categoryId);
        product.setCategory(category);

        ProductStatus productStatus = em.find(ProductStatus.class, 1);
        product.setStatus(productStatus);

        VendorRepository vendorRepository = new VendorRepository();
        VendorProfile vendor = vendorRepository.findVendorInId(uid);

        product.setVendor(vendor);

        // Handle Image Saving
        if (imageStream != null && meta.getFileName() != null) {
            String fileName = System.currentTimeMillis() + "_" + meta.getFileName();
            product.setImageUrl(fileName);

            try {
                // This creates the entire folder path if it doesn't exist
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                Files.copy(imageStream, Paths.get(UPLOAD_DIR + fileName), StandardCopyOption.REPLACE_EXISTING);
                product.setImageUrl(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return productRepo.save(product);
    }

    public List<Product> getProductsByVendor(long userId) {

        VendorRepository vendorRepository = new VendorRepository();
        VendorProfile vendor = vendorRepository.findVendorInId(userId);

        return productRepository.findByVendorId(vendor.getId());
    }

    public Product updateProductStatus(long id, String statusName) {
        // You could add validation here (e.g., check if the user has permission)
        return productRepository.updateStatus(id, statusName);
    }
}
