package com.kanbanapp.service;

import com.kanbanapp.entity.Card;
import com.kanbanapp.entity.Notification;
import com.kanbanapp.entity.User;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class NotificationService {

    @Transactional
    public Notification createNotification(Long userId, String type, String message,
                                         Long relatedEntityId, String relatedEntityType) {
        User user = User.findById(userId);
        if (user == null) throw new NotFoundException("Usuário não encontrado");

        Notification notification = new Notification();
        notification.user = user;
        notification.type = type;
        notification.message = message;
        notification.relatedEntityId = relatedEntityId;
        notification.relatedEntityType = relatedEntityType;
        notification.persist();

        return notification;
    }

    public List<Notification> getNotifications(Long userId, Boolean unreadOnly) {
        if (unreadOnly != null && unreadOnly) {
            return Notification.findUnreadByUserId(userId);
        } else {
            return Notification.findByUserId(userId);
        }
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = Notification.findById(notificationId);
        if (notification == null) throw new NotFoundException("Notificação não encontrada");
        if (!notification.user.id.equals(userId)) throw new jakarta.ws.rs.ForbiddenException("Acesso negado");

        notification.isRead = true;
        notification.persist();
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = Notification.findUnreadByUserId(userId);
        for (Notification n : unread) {
            n.isRead = true;
            n.persist();
        }
    }

    // Scheduled task to check for due dates approaching (e.g., tomorrow)
    @Scheduled(every = "1h") // Run every hour
    @Transactional
    public void checkDueDates() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Card> cards = Card.find("dueDate = ?1 and assignee is not null", tomorrow).list();

        for (Card card : cards) {
            // Check if notification already exists for this card and due date
            long existingCount = Notification.count("relatedEntityId = ?1 and type = 'DUE_DATE'", card.id);
            if (existingCount == 0) {
                createNotification(card.assignee.id, "DUE_DATE",
                    "O card '" + card.title + "' vence amanhã (" + card.dueDate + ")",
                    card.id, "CARD");
            }
        }
    }
}