package com.kanbanapp.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "keycloak_id", nullable = false, length = 36)
    public String keycloakId;

    @Column(nullable = false, length = 50)
    public String type; // CARD_ASSIGNED, DUE_DATE

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
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

    public static List<Notification> findByKeycloakId(String keycloakId) {
        return find("keycloakId", keycloakId).list();
    }

    public static List<Notification> findUnreadByKeycloakId(String keycloakId) {
        return find("keycloakId = ?1 and isRead = false", keycloakId).list();
    }
}
