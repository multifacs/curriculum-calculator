package com.portal.model;

public class Group {

    private Integer groupId;
    private String groupNum;
    private Integer studentsNum;
    private String direction;
    private String phoneNum;

    public Group() {
    }

    public Group(Integer groupId, String groupNum, Integer studentsNum, String direction, String phoneNum) {
        this.groupId = groupId;
        this.groupNum = groupNum;
        this.studentsNum = studentsNum;
        this.direction = direction;
        this.phoneNum = phoneNum;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public Integer getStudentsNum() {
        return studentsNum;
    }

    public void setStudentsNum(Integer studentsNum) {
        this.studentsNum = studentsNum;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
