package com.kanbanapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "cards")
public class Card extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    public KanbanColumn column;

    @Column(nullable = false, length = 300)
    public String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    public String description;

    @Column(name = "assignee_id", length = 36)
    public String assigneeId;

    @Column(name = "due_date")
    public LocalDate dueDate;

    @Column(length = 20)
    public String priority; // LOW, MEDIUM, HIGH

    @Column(nullable = false)
    public Integer position;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<Attachment> attachments = new ArrayList<>();

    public static List<Card> findByColumnId(Long columnId) {
        return find("column.id = ?1 ORDER BY position ASC", columnId).list();
    }
}
