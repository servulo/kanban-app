package com.kanbanapp.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "attachments")
public class Attachment extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    public Card card;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    public User uploadedBy;

    @Column(name = "file_name", nullable = false, length = 200)
    public String fileName;

    @Column(name = "blob_url", columnDefinition = "TEXT")
    public String blobUrl;

    @Column(name = "uploaded_at")
    public LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        uploadedAt = LocalDateTime.now();
    }

    public static List<Attachment> findByCardId(Long cardId) {
        return find("card.id = ?1", cardId).list();
    }
}