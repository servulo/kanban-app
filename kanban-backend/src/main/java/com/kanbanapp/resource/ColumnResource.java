package com.kanbanapp.resource;

import com.kanbanapp.dto.ColumnDTO;
import com.kanbanapp.entity.KanbanColumn;
import com.kanbanapp.service.ColumnService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/projects/{projectId}/columns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class ColumnResource {

    @Inject
    ColumnService columnService;

    @Inject
    JsonWebToken jwt;

    private Long currentUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    private ColumnDTO.ColumnResponse toResponse(KanbanColumn col) {
        List<ColumnDTO.CardSummary> cards = col.cards.stream()
                .map(c -> new ColumnDTO.CardSummary(
                        c.id,
                        c.title,
                        c.priority,
                        c.position,
                        c.assignee != null ? c.assignee.name : null,
                        c.dueDate != null ? c.dueDate.toString() : null
                ))
                .toList();
        return new ColumnDTO.ColumnResponse(col.id, col.name, col.color, col.position, col.project.id, cards);
    }

    @GET
    public List<ColumnDTO.ColumnResponse> list(@PathParam("projectId") Long projectId) {
        return columnService.listByProject(projectId, currentUserId())
                .stream().map(this::toResponse).toList();
    }

    @POST
    public Response create(@PathParam("projectId") Long projectId, ColumnDTO.CreateRequest request) {
        KanbanColumn column = columnService.create(projectId, request.name, request.color, request.position, currentUserId());
        return Response.status(Response.Status.CREATED).entity(toResponse(column)).build();
    }

    @PUT
    @Path("/{columnId}")
    public Response update(@PathParam("projectId") Long projectId,
                           @PathParam("columnId") Long columnId,
                           ColumnDTO.CreateRequest request) {
        KanbanColumn column = columnService.update(columnId, request.name, request.color, request.position, currentUserId());
        return Response.ok(toResponse(column)).build();
    }

    @DELETE
    @Path("/{columnId}")
    public Response delete(@PathParam("projectId") Long projectId,
                           @PathParam("columnId") Long columnId) {
        columnService.delete(columnId, currentUserId());
        return Response.noContent().build();
    }
}