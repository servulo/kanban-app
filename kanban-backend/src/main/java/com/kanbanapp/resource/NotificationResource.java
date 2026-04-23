package com.kanbanapp.resource;

import com.kanbanapp.dto.NotificationDTO;
import com.kanbanapp.entity.Notification;
import com.kanbanapp.service.NotificationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.stream.Collectors;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class NotificationResource {

    @Inject
    NotificationService notificationService;

    @Inject
    JsonWebToken jwt;

    private Long currentUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    @GET
    public List<NotificationDTO.NotificationResponse> getNotifications(@QueryParam("unreadOnly") Boolean unreadOnly) {
        List<Notification> notifications = notificationService.getNotifications(currentUserId(), unreadOnly);
        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PATCH
    @Path("/{id}/read")
    public Response markAsRead(@PathParam("id") Long id) {
        notificationService.markAsRead(id, currentUserId());
        return Response.ok().build();
    }

    @PATCH
    @Path("/read-all")
    public Response markAllAsRead() {
        notificationService.markAllAsRead(currentUserId());
        return Response.ok().build();
    }

    private NotificationDTO.NotificationResponse toResponse(Notification notification) {
        return new NotificationDTO.NotificationResponse(
                notification.id,
                notification.type,
                notification.message,
                notification.isRead,
                notification.createdAt,
                notification.relatedEntityId,
                notification.relatedEntityType
        );
    }
}