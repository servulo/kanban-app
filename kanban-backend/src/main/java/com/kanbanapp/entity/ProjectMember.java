package com.kanbanapp.entity;

import java.time.LocalDateTime;

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

@Entity
@Table(name = "project_members")
public class ProjectMember extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    public Project project;

    @Column(name = "keycloak_id", nullable = false, length = 36)
    public String keycloakId;

    @Column(nullable = false, length = 20)
    public String role;

    @Column(name = "joined_at")
    public LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        joinedAt = LocalDateTime.now();
    }

    public static ProjectMember findByProjectAndKeycloakId(Long projectId, String keycloakId) {
        return find("project.id = ?1 and keycloakId = ?2", projectId, keycloakId).firstResult();
    }
}
