package com.kanbanapp.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false, length = 50)
    public String type; // CARD_ASSIGNED, DUE_DATE, MENTION

    @Column(nullable = false, columnDefinition = "TEXT")
    public String message;

    @Column(name = "is_read", nullable = false)
    public boolean isRead = false;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "related_entity_id")
    public Long relatedEntityId;

    @Column(name = "related_entity_type", length = 50)
    public String relatedEntityType; // CARD, etc.

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static List<Notification> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public static List<Notification> findUnreadByUserId(Long userId) {
        return find("user.id = ?1 and isRead = false", userId).list();
    }
}