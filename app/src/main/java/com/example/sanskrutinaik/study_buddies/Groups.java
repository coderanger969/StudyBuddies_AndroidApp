package com.example.sanskrutinaik.study_buddies;

public class Groups {

    private long groupId;
    private String groupName;
    private String groupCourse;
    private String groupLevel;
    private String groupDescription;
    public String createdDate;

    public Groups(){

    }

    public Groups(String name, String course, String level, String groupDescription, String createdDate){
        this.groupName = name;
        this.groupCourse = course;
        this.groupLevel = level;
        this.groupDescription = groupDescription;
        this.createdDate = createdDate;
    }

    public void setGroupId(long id){
        this.groupId = id;
    }

    public long getGroupId(){
        return groupId;
    }

    public void setGroupName(String name){
        this.groupName = name;
    }

    public String getGroupName(){
        return groupName;
    }

    public void setGroupCourse(String course){
        this.groupCourse = course;
    }

    public String getGroupCourse(){
        return groupCourse;
    }

    public void setGroupLevel(String level){
        this.groupLevel = level;
    }

    public String getGroupLevel(){
        return groupLevel;
    }

    public String getGroupDescription(){
        return groupDescription;
    }
    public String getCreatedDate(){
        return createdDate;
    }
}