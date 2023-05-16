package com.portal.model;

public class SemesterHoursData {
    public String subject = "";
    public Integer semester = 0;
    public Integer lectureHours = 0;
    public Integer practiceHours = 0;

    public String getSubject() {
        return subject;
    }

    public int getSemester() {
        return semester;
    }

    public int getLectureHours() {
        return lectureHours;
    }

    public int getPracticeHours() {
        return practiceHours;
    }
}
