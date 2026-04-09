package com.kanbanapp.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_members")
public class ProjectMember extends PanacheEntity{

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    public Project project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false, length = 20)
    public String role;

    @Column(name = "joined_at")
    public LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        joinedAt = LocalDateTime.now();
    }

    public static ProjectMember findByProjectAndUser(Long projectId, Long userId) {
        return find("project.id = ?1 and user.id = ?2", projectId, userId).firstResult();
    }
    
}
