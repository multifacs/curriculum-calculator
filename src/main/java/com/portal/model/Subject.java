package com.portal.model;

public class Subject {
    private Integer subjectId;
    private String subjectName;
    private String description;
    private Integer professorId;

    public Subject() {
    }

    public Subject(Integer subjectId, String subjectName, String description, Integer professorId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.description = description;
        this.professorId = professorId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Integer professorId) {
        this.professorId = professorId;
    }
}
