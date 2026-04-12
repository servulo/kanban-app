package com.kanbanapp.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class Project extends PanacheEntity {

    @Column(nullable = false, length = 100)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    public User owner;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<ProjectMember> members = new java.util.ArrayList<>();    
    
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }

    public static List<Project> findByUserId(Long userId) {
        return find("SELECT p FROM Project p JOIN p.members m WHERE m.user.id = ?1", userId).list();
    }
    
}
