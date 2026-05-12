package com.kanbanapp.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "attachments")
public class Attachment extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    public Card card;

    @Column(name = "uploaded_by", nullable = false, length = 36)
    public String uploadedBy;

    @Column(name = "file_name", nullable = false, length = 200)
    public String fileName;

    @Column(name = "blob_url", columnDefinition = "NVARCHAR(MAX)")
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
