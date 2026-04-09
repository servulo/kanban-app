package com.kanbanapp.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @Column(nullable = false, length = 100)
    public String name;

    @Column(nullable = false, unique = true, length = 200)
    public String email;

    @Column(name = "password_hash", nullable = false, length = 500)
    public String passwordHash;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }

}