package com.kanbanapp.resource;

import com.kanbanapp.dto.ProjectDTO;
import com.kanbanapp.entity.Project;
import com.kanbanapp.entity.ProjectMember;
import com.kanbanapp.service.ProjectService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

@Path("/v1/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class ProjectResource {

    @Inject
    ProjectService projectService;

    @Inject
    JsonWebToken jwt;

    private String currentKeycloakId() {
        return jwt.getSubject();
    }

    private ProjectDTO.ProjectResponse toResponse(Project p) {
        List<ProjectDTO.MemberResponse> members = p.members.stream()
                .map(m -> new ProjectDTO.MemberResponse(m.keycloakId, m.role))
                .toList();
        return new ProjectDTO.ProjectResponse(p.id, p.name, p.description, p.ownerId, p.createdAt, members);
    }

    @GET
    public List<ProjectDTO.ProjectResponse> list() {
        return projectService.listByKeycloakId(currentKeycloakId())
                .stream().map(this::toResponse).toList();
    }

    @POST
    public Response create(ProjectDTO.CreateRequest request) {
        Project project = projectService.create(request.name, request.description, currentKeycloakId());
        return Response.status(Response.Status.CREATED).entity(toResponse(project)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ProjectDTO.CreateRequest request) {
        Project project = projectService.update(id, request.name, request.description, currentKeycloakId());
        return Response.ok(toResponse(project)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        projectService.delete(id, currentKeycloakId());
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/members")
    public Response addMember(@PathParam("id") Long id, Map<String, String> body) {
        try {
            ProjectMember member = projectService.addMember(
                    id, body.get("keycloakId"), body.get("role"), currentKeycloakId());
            return Response.status(Response.Status.CREATED)
                    .entity(new ProjectDTO.MemberResponse(member.keycloakId, member.role))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}/members/{keycloakId}")
    public Response removeMember(@PathParam("id") Long id, @PathParam("keycloakId") String keycloakId) {
        projectService.removeMember(id, keycloakId, currentKeycloakId());
        return Response.noContent().build();
    }
}
