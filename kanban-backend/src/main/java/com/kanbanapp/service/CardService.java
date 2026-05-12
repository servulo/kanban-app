package com.kanbanapp.service;

import com.kanbanapp.entity.Card;
import com.kanbanapp.entity.KanbanColumn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class CardService {

    @Inject
    ProjectService projectService;

    @Inject
    NotificationService notificationService;

    public List<Card> listByColumn(Long columnId, String keycloakId) {
        KanbanColumn column = KanbanColumn.findById(columnId);
        if (column == null) throw new NotFoundException("Coluna não encontrada");
        projectService.checkMember(column.project.id, keycloakId);
        return Card.findByColumnId(columnId);
    }

    @Transactional
    public Card create(Long columnId, String title, String description, String assigneeId,
                       String dueDate, String priority, Integer position, String keycloakId) {
        KanbanColumn column = KanbanColumn.findById(columnId);
        if (column == null) throw new NotFoundException("Coluna não encontrada");
        projectService.checkMember(column.project.id, keycloakId);

        Card card = new Card();
        card.column = column;
        card.title = title;
        card.description = description;
        card.priority = priority != null ? priority : "MEDIUM";
        card.position = position != null ? position : 0;
        card.assigneeId = assigneeId;

        if (dueDate != null && !dueDate.isBlank()) {
            card.dueDate = LocalDate.parse(dueDate);
        }

        card.persist();

        if (card.assigneeId != null) {
            notificationService.createNotification(card.assigneeId, "CARD_ASSIGNED",
                "Você foi atribuído ao card '" + card.title + "'",
                card.id, "CARD");
        }

        return Card.findById(card.id);
    }

    @Transactional
    public Card update(Long cardId, String title, String description, String assigneeId,
                       String dueDate, String priority, Integer position, String keycloakId) {
        Card card = Card.findById(cardId);
        if (card == null) throw new NotFoundException("Card não encontrado");
        projectService.checkMember(card.column.project.id, keycloakId);

        String oldAssigneeId = card.assigneeId;

        card.title = title;
        card.description = description;
        card.priority = priority;
        if (position != null) card.position = position;
        card.assigneeId = assigneeId;

        if (dueDate != null && !dueDate.isBlank()) {
            card.dueDate = LocalDate.parse(dueDate);
        } else {
            card.dueDate = null;
        }

        if (card.assigneeId != null && !card.assigneeId.equals(oldAssigneeId)) {
            notificationService.createNotification(card.assigneeId, "CARD_ASSIGNED",
                "Você foi atribuído ao card '" + card.title + "'",
                card.id, "CARD");
        }

        return Card.findById(cardId);
    }

    @Transactional
    public Card move(Long cardId, Long targetColumnId, Integer position, String keycloakId) {
        Card card = Card.findById(cardId);
        if (card == null) throw new NotFoundException("Card não encontrado");
        projectService.checkMember(card.column.project.id, keycloakId);

        KanbanColumn targetColumn = KanbanColumn.findById(targetColumnId);
        if (targetColumn == null) throw new NotFoundException("Coluna destino não encontrada");

        card.column = targetColumn;
        if (position != null) card.position = position;

        return Card.findById(cardId);
    }

    @Transactional
    public void delete(Long cardId, String keycloakId) {
        Card card = Card.findById(cardId);
        if (card == null) throw new NotFoundException("Card não encontrado");
        projectService.checkMember(card.column.project.id, keycloakId);
        card.delete();
    }
}
