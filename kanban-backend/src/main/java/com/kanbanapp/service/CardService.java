package com.kanbanapp.service;

import com.kanbanapp.entity.Card;
import com.kanbanapp.entity.KanbanColumn;
import com.kanbanapp.entity.User;
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

    public List<Card> listByColumn(Long columnId, Long requesterId) {
        KanbanColumn column = KanbanColumn.findById(columnId);
        if (column == null) throw new NotFoundException("Coluna não encontrada");
        projectService.checkMember(column.project.id, requesterId);
        return Card.findByColumnId(columnId);
    }

    @Transactional
    public Card create(Long columnId, String title, String description, Long assigneeId,
                       String dueDate, String priority, Integer position, Long requesterId) {
        KanbanColumn column = KanbanColumn.findById(columnId);
        if (column == null) throw new NotFoundException("Coluna não encontrada");
        projectService.checkMember(column.project.id, requesterId);

        Card card = new Card();
        card.column = column;
        card.title = title;
        card.description = description;
        card.priority = priority != null ? priority : "MEDIUM";
        card.position = position != null ? position : 0;

        if (assigneeId != null) {
            User assignee = User.findById(assigneeId);
            if (assignee == null) throw new NotFoundException("Usuário não encontrado");
            card.assignee = assignee;
        }

        if (dueDate != null && !dueDate.isBlank()) {
            card.dueDate = LocalDate.parse(dueDate);
        }

        card.persist();

        if (card.assignee != null) {
            notificationService.createNotification(card.assignee.id, "CARD_ASSIGNED",
                "Você foi atribuído ao card '" + card.title + "'",
                card.id, "CARD");
        }

        return Card.findById(card.id);
    }

    @Transactional
    public Card update(Long cardId, String title, String description, Long assigneeId,
                       String dueDate, String priority, Integer position, Long requesterId) {
        Card card = Card.findById(cardId);
        if (card == null) throw new NotFoundException("Card não encontrado");
        projectService.checkMember(card.column.project.id, requesterId);

        Long oldAssigneeId = card.assignee != null ? card.assignee.id : null;

        card.title = title;
        card.description = description;
        card.priority = priority;
        if (position != null) card.position = position;

        if (assigneeId != null) {
            User assignee = User.findById(assigneeId);
            if (assignee == null) throw new NotFoundException("Usuário não encontrado");
            card.assignee = assignee;
        } else {
            card.assignee = null;
        }

        if (dueDate != null && !dueDate.isBlank()) {
            card.dueDate = LocalDate.parse(dueDate);
        } else {
            card.dueDate = null;
        }

        if (card.assignee != null && !card.assignee.id.equals(oldAssigneeId)) {
            notificationService.createNotification(card.assignee.id, "CARD_ASSIGNED",
                "Você foi atribuído ao card '" + card.title + "'",
                card.id, "CARD");
        }

        return Card.findById(cardId);
    }

    @Transactional
    public Card move(Long cardId, Long targetColumnId, Integer position, Long requesterId) {
        Card card = Card.findById(cardId);
        if (card == null) throw new NotFoundException("Card não encontrado");
        projectService.checkMember(card.column.project.id, requesterId);

        KanbanColumn targetColumn = KanbanColumn.findById(targetColumnId);
        if (targetColumn == null) throw new NotFoundException("Coluna destino não encontrada");

        card.column = targetColumn;
        if (position != null) card.position = position;

        return Card.findById(cardId);
    }

    @Transactional
    public void delete(Long cardId, Long requesterId) {
        Card card = Card.findById(cardId);
        if (card == null) throw new NotFoundException("Card não encontrado");
        projectService.checkMember(card.column.project.id, requesterId);
        card.delete();
    }
}