package com.kanbanapp.resource;

import com.kanbanapp.dto.CardDTO;
import com.kanbanapp.entity.Attachment;
import com.kanbanapp.entity.Card;
import com.kanbanapp.entity.User;
import com.kanbanapp.service.ProjectService;
import com.kanbanapp.service.StorageService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@Path("/cards/{cardId}/attachments")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class AttachmentResource {

    public static class UploadForm {
        @RestForm("file")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        public FileUpload file;
    }

    @Inject
    StorageService storageService;

    @Inject
    ProjectService projectService;

    @Inject
    JsonWebToken jwt;

    private Long currentUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    @GET
    public List<CardDTO.AttachmentSummary> list(@PathParam("cardId") Long cardId) {
        Card card = Card.findById(cardId);
        if (card == null) throw new NotFoundException("Card não encontrado");
        projectService.checkMember(card.column.project.id, currentUserId());

        return Attachment.findByCardId(cardId).stream()
                .map(a -> new CardDTO.AttachmentSummary(
                        a.id,
                        a.fileName,
                        a.blobUrl,
                        a.uploadedAt != null ? a.uploadedAt.toString() : null
                ))
                .toList();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response upload(@PathParam("cardId") Long cardId, @MultipartForm UploadForm form) {
        try {
            Card card = Card.findById(cardId);
            if (card == null) throw new NotFoundException("Card não encontrado");
            projectService.checkMember(card.column.project.id, currentUserId());

            if (form.file == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Nenhum arquivo enviado").build();
            }

            String fileName = form.file.fileName();
            byte[] fileBytes = Files.readAllBytes(form.file.filePath());
            InputStream fileStream = new java.io.ByteArrayInputStream(fileBytes);
            String blobUrl = storageService.upload(fileName, fileStream, fileBytes.length, "application/octet-stream");

            User uploader = User.findById(currentUserId());

            Attachment attachment = new Attachment();
            attachment.card = card;
            attachment.uploadedBy = uploader;
            attachment.fileName = fileName;
            attachment.blobUrl = blobUrl;
            attachment.persist();

            return Response.status(Response.Status.CREATED)
                    .entity(new CardDTO.AttachmentSummary(
                            attachment.id,
                            attachment.fileName,
                            attachment.blobUrl,
                            attachment.uploadedAt != null ? attachment.uploadedAt.toString() : null
                    ))
                    .build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao fazer upload: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{attachmentId}")
    @Transactional
    public Response delete(@PathParam("cardId") Long cardId,
                           @PathParam("attachmentId") Long attachmentId) {
        Attachment attachment = Attachment.findById(attachmentId);
        if (attachment == null) throw new NotFoundException("Anexo não encontrado");

        projectService.checkMember(attachment.card.column.project.id, currentUserId());
        storageService.delete(attachment.blobUrl);
        attachment.delete();

        return Response.noContent().build();
    }
}