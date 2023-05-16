package com.portal.model;

import java.time.LocalDate;
import java.util.List;

public class Professor {
    private Integer professorId;
    private String fullName;
    private String phoneNum;
    private LocalDate birthDate;
    private String address;
    private String email;
    private List<Integer> subjects;

    public Professor() {
    }

    public Professor(Integer professorId, String fullName, String phoneNum, LocalDate birthDate, String address, String email, List<Integer> subjects) {
        this.professorId = professorId;
        this.fullName = fullName;
        this.phoneNum = phoneNum;
        this.birthDate = birthDate;
        this.address = address;
        this.email = email;
        this.subjects = subjects;
    }

    public Integer getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Integer professorId) {
        this.professorId = professorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Integer> subjects) {
        this.subjects = subjects;
    }
}
