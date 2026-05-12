package com.kanbanapp.service;

import com.kanbanapp.entity.Card;
import com.kanbanapp.entity.Notification;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class NotificationService {

    @Transactional
    public Notification createNotification(String keycloakId, String type, String message,
                                           Long relatedEntityId, String relatedEntityType) {
        Notification notification = new Notification();
        notification.keycloakId = keycloakId;
        notification.type = type;
        notification.message = message;
        notification.relatedEntityId = relatedEntityId;
        notification.relatedEntityType = relatedEntityType;
        notification.persist();

        return notification;
    }

    public List<Notification> getNotifications(String keycloakId, Boolean unreadOnly) {
        if (unreadOnly != null && unreadOnly) {
            return Notification.findUnreadByKeycloakId(keycloakId);
        } else {
            return Notification.findByKeycloakId(keycloakId);
        }
    }

    @Transactional
    public void markAsRead(Long notificationId, String keycloakId) {
        Notification notification = Notification.findById(notificationId);
        if (notification == null) throw new NotFoundException("Notificação não encontrada");
        if (!notification.keycloakId.equals(keycloakId)) throw new ForbiddenException("Acesso negado");

        notification.isRead = true;
        notification.persist();
    }

    @Transactional
    public void markAllAsRead(String keycloakId) {
        List<Notification> unread = Notification.findUnreadByKeycloakId(keycloakId);
        for (Notification n : unread) {
            n.isRead = true;
            n.persist();
        }
    }

    @Scheduled(every = "1h")
    @Transactional
    public void checkDueDates() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Card> cards = Card.find("dueDate = ?1 and assigneeId is not null", tomorrow).list();

        for (Card card : cards) {
            long existingCount = Notification.count(
                "relatedEntityId = ?1 and type = 'DUE_DATE'", card.id);
            if (existingCount == 0) {
                createNotification(card.assigneeId, "DUE_DATE",
                    "O card '" + card.title + "' vence amanhã (" + card.dueDate + ")",
                    card.id, "CARD");
            }
        }
    }
}
