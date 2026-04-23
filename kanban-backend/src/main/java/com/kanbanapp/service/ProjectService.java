package com.kanbanapp.service;

import java.util.List;

import com.kanbanapp.entity.Project;
import com.kanbanapp.entity.ProjectMember;
import com.kanbanapp.entity.User;

import io.quarkus.security.ForbiddenException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ProjectService {

@Transactional
public Project create(String name, String description, Long ownerId) {
    User owner = User.findById(ownerId);
    if (owner == null) throw new NotFoundException("Usuário não encontrado");

    Project project = new Project();
    project.name = name;
    project.description = description;
    project.owner = owner;
    project.persist();

    ProjectMember member = new ProjectMember();
    member.project = project;
    member.user = owner;
    member.role = "ADMIN";
    member.persist();

    // Recarrega do banco para garantir que members está populado
    return Project.findById(project.id);
}

    public List<Project> listByUser(Long userId) {
        return Project.findByUserId(userId);
    }

    @Transactional
    public Project update(Long projectId, String name, String description, Long requesterId) {
        Project project = Project.findById(projectId);
        if (project == null) throw new NotFoundException("Projeto não encontrado");
        checkAdmin(projectId, requesterId);

        project.name = name;
        project.description = description;

        // Recarrega para garantir relacionamentos populados
        return Project.findById(projectId);
    }

    @Transactional
    public void delete(Long projectId, Long requesterId) {
        Project project = Project.findById(projectId);
        if (project == null) throw new NotFoundException("Projeto não encontrado");
        checkAdmin(projectId, requesterId);
        project.delete();
    }

    @Transactional
    public ProjectMember addMember(Long projectId, String email, String role, Long requesterId) {
        checkAdmin(projectId, requesterId);
        
        User user = User.findByEmail(email);
        if (user == null) throw new NotFoundException("Usuário não encontrado");

        Project project = Project.findById(projectId);

        if(ProjectMember.findByProjectAndUser(projectId, user.id) != null) {
            throw new IllegalArgumentException("Usuário já é membro do projeto");
        }

        ProjectMember member = new ProjectMember();

        member.project = project;
        member.user = user;
        member.role = role != null ? role : "MEMBER";

        member.persist();

        return member;
            
     }

    @Transactional
    public void removeMember(Long projectId, Long userId, Long requesterId) {
        checkAdmin(projectId, requesterId);
        ProjectMember member = ProjectMember.findByProjectAndUser(projectId, userId);
        if (member == null) throw new NotFoundException("Membro não encontrado");
        member.delete();
    }     

    public void checkMember(Long projectId, Long userId) {
        if(ProjectMember.findByProjectAndUser(projectId, userId) == null) {
            throw new ForbiddenException("Acesso negado ao projeto");
        }
    }

    public void checkAdmin(Long projectId, Long userId) {
        ProjectMember member = ProjectMember.findByProjectAndUser(projectId, userId);
        if(member ==null || !"ADMIN".equals(member.role)) {
            throw new ForbiddenException("Apenas administradores podem executar essa ação");
        }
    }
    
}
