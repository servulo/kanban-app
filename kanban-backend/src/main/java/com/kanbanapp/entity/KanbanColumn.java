package com.kanbanapp.entity;

import java.util.ArrayList;
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
@Table(name = "columns")
public class KanbanColumn extends PanacheEntity {
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    public Project project;

    @Column(nullable = false, length = 100)
    public String name;

    @Column(length = 7)
    public String color;

    @Column(nullable = false)
    public Integer position;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    public List<Card> cards = new ArrayList<>();

    public static List<KanbanColumn> findByProjectId(Long projectId) {
        return find("project.id = ?1 ORDER BY position ASC", projectId).list();
    }

}
