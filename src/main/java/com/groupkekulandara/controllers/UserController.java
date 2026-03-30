package com.groupkekulandara.controllers;

import com.groupkekulandara.dto.UserResponseDTO;
import com.groupkekulandara.models.User;
import com.groupkekulandara.models.UserVerification;
import com.groupkekulandara.services.UserService;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import java.util.Map;

@Path("/user")
public class UserController {

    private final UserService userService = new UserService();
    @POST
    @Path("/signIn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userSignIn(User user) {
        // Call the Service (which uses the Repository)
        UserResponseDTO responseDto = userService.userSignIn(user);

        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new UserResponseDTO(false, "Invalid email or password", null, null))
                    .build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {

        // One command to rule them all
        UserResponseDTO result = userService.processFullRegistration(user);

        if (!result.isSuccess()) {
            // If it's a conflict (email exists), return 409, else 500
            int status = result.getMessage().contains("exists") ? 409 : 500;
            return Response.status(status).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @POST
    @Path("/verification-code")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificationCode(JsonObject jsonObject) {
        // Make sure these keys "verification_code" match your Android Map/JSON keys!
        String verificationCode = jsonObject.getString("verification_code");
        long userId = jsonObject.getJsonNumber("user_id").longValue();

        // Call the service and get a DTO back
        UserResponseDTO response = userService.verifyUser(verificationCode, userId);

        if (response.isSuccess()) {
            return Response.ok(response).build(); // 200 OK
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .build();
        }
    }

    @POST
    @Path("/register-final")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userRegistrationFinal(JsonObject jsonObject) {
        // 1. Validation
        if (!jsonObject.containsKey("user_id") || !jsonObject.containsKey("mobile")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new UserResponseDTO(false, "Missing required info"))
                    .build();
        }

        long userId = jsonObject.getJsonNumber("user_id").longValue();
        String firstName = jsonObject.getString("first_name");
        String lastName = jsonObject.getString("last_name");
        String mobile = jsonObject.getString("mobile");

//         2. Call Service
        UserResponseDTO response = userService.registerFinal(userId, firstName, lastName, mobile);
        return Response.ok(response).build();
    }

    @POST
    @Path("/update-profile/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(
            @PathParam("id") long userId,
            @FormDataParam("firstName") String firstName,
            @FormDataParam("lastName") String lastName,
            @FormDataParam("phoneNumber") String phoneNumber,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        // 1. Get the actual filename from the detail object
        String fileName = (fileDetail != null) ? fileDetail.getFileName() : null;

        // 2. Call service and get the updated User object
        User updatedUser = userService.handleProfileUpdate(userId, firstName, lastName, phoneNumber, fileInputStream, fileName);

        if (updatedUser != null) {
            // Return 200 OK with the updated user data
            return Response.ok(updatedUser).build();
        } else {
            // Return 404 if user wasn't found in DB
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found").build();
        }
    }

    @POST
    @Path("check-login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = userService.authenticate(email, password);

        if (user != null) {
            // Remove password from object before sending to frontend for security
            user.setPassword(null);
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid credentials\"}").build();
        }
    }
}