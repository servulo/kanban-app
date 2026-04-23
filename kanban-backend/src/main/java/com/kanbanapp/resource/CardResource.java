package com.kanbanapp.resource;

import com.kanbanapp.dto.CardDTO;
import com.kanbanapp.entity.Card;
import com.kanbanapp.service.CardService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/cards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class CardResource {

    @Inject
    CardService cardService;

    @Inject
    JsonWebToken jwt;

    private Long currentUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    private CardDTO.CardResponse toResponse(Card card) {
        List<CardDTO.AttachmentSummary> attachments = card.attachments.stream()
                .map(a -> new CardDTO.AttachmentSummary(
                        a.id,
                        a.fileName,
                        a.blobUrl,
                        a.uploadedAt != null ? a.uploadedAt.toString() : null
                ))
                .toList();

        return new CardDTO.CardResponse(
                card.id,
                card.title,
                card.description,
                card.column.id,
                card.assignee != null ? card.assignee.id : null,
                card.assignee != null ? card.assignee.name : null,
                card.dueDate != null ? card.dueDate.toString() : null,
                card.priority,
                card.position,
                card.createdAt != null ? card.createdAt.toString() : null,
                attachments
        );
    }

    @GET
    @Path("/column/{columnId}")
    public List<CardDTO.CardResponse> listByColumn(@PathParam("columnId") Long columnId) {
        return cardService.listByColumn(columnId, currentUserId())
                .stream().map(this::toResponse).toList();
    }

    @POST
    public Response create(CardDTO.CreateRequest request) {
        Card card = cardService.create(
                request.columnId != null ? request.columnId : null,
                request.title, request.description, request.assigneeId,
                request.dueDate, request.priority, request.position, currentUserId()
        );
        return Response.status(Response.Status.CREATED).entity(toResponse(card)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, CardDTO.CreateRequest request) {
        Card card = cardService.update(id, request.title, request.description,
                request.assigneeId, request.dueDate, request.priority, request.position, currentUserId());
        return Response.ok(toResponse(card)).build();
    }

    @PATCH
    @Path("/{id}/move")
    public Response move(@PathParam("id") Long id, CardDTO.MoveRequest request) {
        Card card = cardService.move(id, request.columnId, request.position, currentUserId());
        return Response.ok(toResponse(card)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        cardService.delete(id, currentUserId());
        return Response.noContent().build();
    }
}