package com.groupkekulandara.controllers;

import com.groupkekulandara.models.*;
import com.groupkekulandara.services.AdminService;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("admin")
public class AdminController {

    private final AdminService adminService = new AdminService();

    @GET
    @Path("/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStats() {
        Map<String, Object> data = adminService.getDashboardOverview();
        return Response.ok(data).build();
    }


    @GET
    @Path("/vendors")
    public Response getAllVendors() {
        List<Map<String, Object>> vendors = adminService.getAllVendorsList();
        return Response.ok(vendors).build();
    }

    @POST
    @Path("/vendor-verify/{id}")
    public Response verifyVendor(@PathParam("id") int id) {
        boolean updated = adminService.verifyVendorStatus(id);
        if (updated) {
            return Response.ok("{\"message\":\"Vendor verified\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Vendor not found").build();
        }
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        try {
            List<User> users = adminService.getAllUsersForDropdown();
            return Response.ok(users).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Could not load users\"}").build();
        }
    }

    @POST
    @Path("/vendor-add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVendor(Map<String, Object> data) {
        String result = adminService.validateAndSaveVendor(data);

        if (result.equals("SUCCESS")) {
            return Response.ok("{\"message\":\"Vendor added\"}").build();
        } else {
            // Returns 400 Bad Request with the specific error message
            return Response.status(400)
                    .entity("{\"error\":\"" + result + "\"}").build();
        }
    }


    @POST
    @Path("/users/status/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleUserStatus(
            @PathParam("id") int userId,
            @QueryParam("active") int status) {

        String result = adminService.changeUserStatus(userId, status);

        if (result.equals("SUCCESS")) {
            String action = (status == 2) ? "Unblocked" : "Blocked";
            return Response.ok("{\"message\":\"User " + action + " successfully\"}").build();
        } else if (result.equals("INVALID_STATUS")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid status value provided\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"User ID not found in database\"}").build();
        }
    }


    // GET: /api/products
    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts() {
        List<Product> products = adminService.getAllProducts();
        return Response.ok(products).build();
    }

    // GET: /api/categories
    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories() {
        List<Category> categories = adminService.getAllCategories();
        return Response.ok(categories).build();
    }

    // POST: /api/admin/categories
    @POST
    @Path("/save-categories")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCategory(Map<String, String> payload) {
        String name = payload.get("name");
        boolean success = adminService.addCategory(name);

        if (success) {
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"Category created\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Failed to create category\"}").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("orders")
    public Response getAllOrders() {
        try {
            List<OrderItem> orders = adminService.getAllOrders();
            return Response.ok(orders).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Could not retrieve orders\"}")
                    .build();
        }
    }
}
