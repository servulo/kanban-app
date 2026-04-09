package com.kanbanapp.resource;

import com.kanbanapp.dto.AuthDTO;
import com.kanbanapp.entity.User;
import com.kanbanapp.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register(AuthDTO.RegisterRequest request) {
        try {
            User user = authService.register(request.name, request.email, request.password);
            String token = authService.login(request.email, request.password);
            return Response.status(Response.Status.CREATED)
                    .entity(new AuthDTO.AuthResponse(token, user.name, user.email, user.id))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(AuthDTO.LoginRequest request) {
        try {
            User user = User.findByEmail(request.email);
            String token = authService.login(request.email, request.password);
            return Response.ok(new AuthDTO.AuthResponse(token, user.name, user.email, user.id))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }
    }
}