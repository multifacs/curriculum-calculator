package com.portal.model;

public class Curriculum {
    private Integer curriculumId;
    private Integer lectureHours;
    private Integer practiceHours;
    private String direction;
    private Integer subjectId;
    private Integer semester;

    public Curriculum() {
    }

    public Curriculum(Integer curriculumId, Integer lectureHours, Integer practiceHours, String direction, Integer subjectId, Integer semester) {
        this.curriculumId = curriculumId;
        this.lectureHours = lectureHours;
        this.practiceHours = practiceHours;
        this.direction = direction;
        this.subjectId = subjectId;
        this.semester = semester;
    }

    public Integer getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(Integer curriculumId) {
        this.curriculumId = curriculumId;
    }

    public Integer getLectureHours() {
        return lectureHours;
    }

    public void setLectureHours(Integer lectureHours) {
        this.lectureHours = lectureHours;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Integer practiceHours) {
        this.practiceHours = practiceHours;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }
}
