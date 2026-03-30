package com.groupkekulandara.controllers;

import com.groupkekulandara.dto.CartItemDTO;
import com.groupkekulandara.dto.OrderDTO;
import com.groupkekulandara.dto.OrderRequest;
import com.groupkekulandara.dto.UserResponseDTO;
import com.groupkekulandara.models.Order;
import com.groupkekulandara.models.OrderItem;
import com.groupkekulandara.services.OrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/order")
public class OrderController {

    private final OrderService orderService = new OrderService();

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrder(OrderRequest request) {
        // Validation: Ensure the cart isn't empty
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cannot process an empty order.")
                    .build();
        }

        boolean success = orderService.saveCompleteOrder(request);

        if (success) {
            // Log the success for the server administrator
            System.out.println("Updating system: New order created for User " + request.getUserId());
            return Response.ok("Order saved successfully").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database synchronization failed.")
                    .build();
        }
    }

    @GET
    @Path("user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserOrderHistory(@PathParam("userId") long userId) {
        try {
            List<OrderDTO> history = orderService.getHistory(userId);

            // Professional log for Glassfish
            System.out.println("Updating dashboard: Loaded " + history.size() + " orders for User " + userId);
            return Response.ok(history).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating order history")
                    .build();
        }
    }

    @GET
    @Path("getOrderItem/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderItems(@PathParam("id") Long id) {
        try {
            List<OrderItem> items = orderService.getItemsByOrderId(id);
            if (items.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(items).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("updateStatus/{id}/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrderStatus(@PathParam("id") Long id, @PathParam("status") String status) {
        try {
            boolean updated = orderService.updateOrderStatus(id, status);
            if (updated) {
                return Response.ok(new UserResponseDTO(true, "Status updated to " + status, null, null)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("vendor/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByVendor(@PathParam("id") Long vendorId) {
        try {
            List<Order> orders = orderService.getOrdersByVendorId(vendorId);
            return Response.ok(orders).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }
}
