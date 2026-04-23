package com.kanbanapp.dto;

import java.util.List;

public class ColumnDTO {

    public static class CreateRequest {
        public String name;
        public String color;
        public Integer position;
    }

    public static class CardSummary {
        public Long id;
        public String title;
        public String priority;
        public Integer position;
        public String assigneeName;
        public String dueDate;

        public CardSummary(Long id, String title, String priority, Integer position, String assigneeName, String dueDate) {
            this.id = id;
            this.title = title;
            this.priority = priority;
            this.position = position;
            this.assigneeName = assigneeName;
            this.dueDate = dueDate;
        }
    }

    public static class ColumnResponse {
        public Long id;
        public String name;
        public String color;
        public Integer position;
        public Long projectId;
        public List<CardSummary> cards;

        public ColumnResponse(Long id, String name, String color, Integer position, Long projectId, List<CardSummary> cards) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.position = position;
            this.projectId = projectId;
            this.cards = cards;
        }
    }
}