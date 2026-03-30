package com.groupkekulandara.controllers;

import com.groupkekulandara.dto.ProductDTO;
import com.groupkekulandara.models.Product;
import com.groupkekulandara.services.ProductService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.InputStream;
import java.util.List;

import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("product")
public class ProductController {

    // Initialize the service that handles the Entity -> DTO mapping
    private final ProductService productService = new ProductService();

    @GET
    @Path("all-products")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProducts() {
        try {
            // 1. Fetch the products with nested Vendor and Category DTOs
            List<ProductDTO> products = productService.getProducts("");

            // 2. Return the list as a JSON array to Android
            return Response.ok(products).build();

        } catch (Exception e) {
            e.printStackTrace();
            // 3. Return a professional error message if Glassfish fails
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating product feed")
                    .build();
        }
    }


    @GET
    @Path("new-arrivals-products")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNewArrivals() {
        try {
            // 1. Fetch the products with nested Vendor and Category DTOs
            List<ProductDTO> products = productService.getNewArrivals();

            // 2. Return the list as a JSON array to Android
            return Response.ok(products).build();

        } catch (Exception e) {
            e.printStackTrace();
            // 3. Return a professional error message if Glassfish fails
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating product feed")
                    .build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchProducts(@QueryParam("name") String name) {
        try {
            List<ProductDTO> products = productService.getProducts(name);
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/add/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(
            @PathParam("id") long userId,
            @FormDataParam("name") String name,
            @FormDataParam("category_id") int categoryId,
            @FormDataParam("price") double price,
            @FormDataParam("stock") int stock,
            @FormDataParam("description") String description,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData) {

        // 1. Create Product Object
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stock);
        product.setDescription(description);

        // 2. Call Service to handle Logic and Image Saving
        Product savedProduct = productService.saveProduct(userId, product, categoryId, fileInputStream, fileMetaData);

        return Response.status(Response.Status.CREATED).entity(savedProduct).build();
    }

    @GET
    @Path("/getVendorProduct/{id}")
    public Response getVendorProducts(@PathParam("id") long userId) {
        List<Product> products = productService.getProductsByVendor(userId);

        products.forEach(t->{
            System.out.println(t.getStatus().getStatus());
        });

        if (products.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.ok(products).build();
    }

    @PUT
    @Path("/updateProductStatus/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStatus(
            @PathParam("id") long id,
            @QueryParam("status") String statusName) {

        Product updatedProduct = productService.updateProductStatus(id, statusName);

        if (updatedProduct != null) {
            return Response.ok(updatedProduct).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Product not found or update failed")
                    .build();
        }
    }
}


