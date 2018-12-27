package com.definesys.angrypecker.pojo;

public class DragonProjectInfo {

    private Integer id;

    private String projectName;

    private String projectLogo;

    private String ownerName;

    private Integer ownerId;

    private String isOwner;

    private Integer gtasksNum;


    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectLogo() {
        return projectLogo;
    }

    public void setProjectLogo(String projectLogo) {
        this.projectLogo = projectLogo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(String isOwner) {
        this.isOwner = isOwner;
    }

    public Integer getGtasksNum() {
        return gtasksNum;
    }

    public void setGtasksNum(Integer gtasksNum) {
        this.gtasksNum = gtasksNum;
    }
}
