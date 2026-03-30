package com.groupkekulandara.controllers;

import com.groupkekulandara.dto.CategoryDTO;
import com.groupkekulandara.models.Category;
import com.groupkekulandara.services.CategoryService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("category") // Base path: api/category
public class CategoryController {

    private final CategoryService categoryService = new CategoryService();

    @GET
    @Path("all-categories") // Full path: api/category/all-categories
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories() {
        try {
            // 1. Get the list of DTOs from the service
            List<CategoryDTO> categories = categoryService.getAllCategories();

            // 2. Return a professional 200 OK response with the JSON data
            return Response.ok(categories).build();

        } catch (Exception e) {
            e.printStackTrace();
            // 3. Return 500 if something goes wrong on the Glassfish server
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating category list").build();
        }
    }
}
