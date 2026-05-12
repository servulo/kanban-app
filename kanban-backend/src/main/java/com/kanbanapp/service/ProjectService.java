package com.kanbanapp.service;

import java.util.List;

import com.kanbanapp.entity.Project;
import com.kanbanapp.entity.ProjectMember;

import io.quarkus.security.ForbiddenException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ProjectService {

    @Transactional
    public Project create(String name, String description, String keycloakId) {
        Project project = new Project();
        project.name = name;
        project.description = description;
        project.ownerId = keycloakId;
        project.persist();

        ProjectMember member = new ProjectMember();
        member.project = project;
        member.keycloakId = keycloakId;
        member.role = "ADMIN";
        member.persist();

        return Project.findById(project.id);
    }

    public List<Project> listByKeycloakId(String keycloakId) {
        return Project.findByKeycloakId(keycloakId);
    }

    @Transactional
    public Project update(Long projectId, String name, String description, String keycloakId) {
        Project project = Project.findById(projectId);
        if (project == null) throw new NotFoundException("Projeto não encontrado");
        checkAdmin(projectId, keycloakId);

        project.name = name;
        project.description = description;

        return Project.findById(projectId);
    }

    @Transactional
    public void delete(Long projectId, String keycloakId) {
        Project project = Project.findById(projectId);
        if (project == null) throw new NotFoundException("Projeto não encontrado");
        checkAdmin(projectId, keycloakId);
        project.delete();
    }

    @Transactional
    public ProjectMember addMember(Long projectId, String memberKeycloakId, String role, String requesterKeycloakId) {
        checkAdmin(projectId, requesterKeycloakId);

        Project project = Project.findById(projectId);
        if (project == null) throw new NotFoundException("Projeto não encontrado");

        if (ProjectMember.findByProjectAndKeycloakId(projectId, memberKeycloakId) != null) {
            throw new IllegalArgumentException("Usuário já é membro do projeto");
        }

        ProjectMember member = new ProjectMember();
        member.project = project;
        member.keycloakId = memberKeycloakId;
        member.role = role != null ? role : "MEMBER";
        member.persist();

        return member;
    }

    @Transactional
    public void removeMember(Long projectId, String memberKeycloakId, String requesterKeycloakId) {
        checkAdmin(projectId, requesterKeycloakId);
        ProjectMember member = ProjectMember.findByProjectAndKeycloakId(projectId, memberKeycloakId);
        if (member == null) throw new NotFoundException("Membro não encontrado");
        member.delete();
    }

    public void checkMember(Long projectId, String keycloakId) {
        if (ProjectMember.findByProjectAndKeycloakId(projectId, keycloakId) == null) {
            throw new ForbiddenException("Acesso negado ao projeto");
        }
    }

    public void checkAdmin(Long projectId, String keycloakId) {
        ProjectMember member = ProjectMember.findByProjectAndKeycloakId(projectId, keycloakId);
        if (member == null || !"ADMIN".equals(member.role)) {
            throw new ForbiddenException("Apenas administradores podem executar essa ação");
        }
    }
}
