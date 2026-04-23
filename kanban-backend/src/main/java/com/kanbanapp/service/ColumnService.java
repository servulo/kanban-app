package com.kanbanapp.service;

import java.util.List;

import com.kanbanapp.entity.KanbanColumn;
import com.kanbanapp.entity.Project;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ColumnService {

    @Inject
    ProjectService projectService;
    
    public List<KanbanColumn> listByProject(Long projectId, Long requesterId) {
        projectService.checkMember(projectId, requesterId);
        return KanbanColumn.findByProjectId(projectId);
    }

    @Transactional
    public KanbanColumn create(Long projectId, String name, String color, Integer position, Long requesterId) {

        projectService.checkAdmin(projectId, requesterId);

        Project project = Project.findById(requesterId);

        if (project == null) throw new NotFoundException("Projeto não encontrado");

        KanbanColumn column = new KanbanColumn();

        column.project = project;
        column.name = name;
        column.color = color != null ? color: "#CCCCCC";
        column.position = position != null ? position: 0;
        
        column.persist();

        return column;
    }

    @Transactional
    public KanbanColumn update(Long columnId, String name, String color, Integer position, Long requesterId) {

        KanbanColumn column = KanbanColumn.findById(columnId);

        if (column == null) throw new NotFoundException("Coluna não encontrada");

        projectService.checkAdmin(column.project.id, requesterId);

        column.name = name;
        if(color != null) column.color = color;
        if(position != null) column.position = position;

        return column;

    }

    @Transactional
    public void delete(Long columnId, Long requesterId) {
        KanbanColumn column = KanbanColumn.findById(columnId);
        if (column == null) throw new NotFoundException("Coluna não encontrada");
        projectService.checkAdmin(column.project.id, requesterId);
        column.delete();
    }
    
}
