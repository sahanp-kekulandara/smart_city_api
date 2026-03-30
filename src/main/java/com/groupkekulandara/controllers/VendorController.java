package com.groupkekulandara.controllers;

import com.groupkekulandara.models.VendorProfile;
import com.groupkekulandara.services.VendorService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/vendor")
public class VendorController {
    private VendorService service = new VendorService();

    @GET
    @Path("/getProfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVendorProfile(@QueryParam("uid") long userId) {
        VendorProfile vendor = service.getProfile(userId);
        if (vendor != null) {
            return Response.ok(vendor).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Profile not found").build();
    }

    @PUT
    @Path("/updateProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateVendorProfile(VendorProfile updatedData) {
        boolean success = service.updateProfile(updatedData.getId(),
                updatedData.getBusinessName(),
                updatedData.getDescription());
        if (success) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/getAllVendor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVendor() {
        List<VendorProfile> vendors = service.getAllVendors();

        if (vendors != null && !vendors.isEmpty()) {
            return Response.ok(vendors).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No vendors found in the database")
                    .build();
        }
    }
}
