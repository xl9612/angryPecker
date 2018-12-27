package com.definesys.angrypecker.pojo;

import com.definesys.mpaas.query.annotation.*;
import com.definesys.mpaas.query.json.MpaasDateDeserializer;
import com.definesys.mpaas.query.json.MpaasDateSerializer;
import com.definesys.mpaas.query.model.MpaasBasePojo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: 阳
 * @since: 2018-11-27
 * @history: 1.2018-11-27 created by 阳
 */
@SQLQuery(value = {
        @SQL(view = "dragonproinfo_v", sql = "SELECT DISTINCT dp.id as id,dp.status,dp.project_desc ,dp.creation_date,dp.project_name,dp.project_logo,dp.project_owner_id, \n" +
                "(SELECT fu.user_name FROM fnd_users fu where fu.id = dp.project_owner_id) ownerName,drm.user_id\n" +
                "FROM `dragon_projects` dp\n" +
                "LEFT JOIN dragon_project_role dpr ON dpr.project_id = dp.id\n" +
                "LEFT JOIN dragon_role_member drm ON drm.role_id = dpr.id"),
}
)
public class DragonProInfo extends MpaasBasePojo {

    @RowID(sequence = "DRAGON_PROJECTS_S", type = RowIDType.AUTO)
    private Integer id;

    @Column(value = "project_name")
    private String projectName;

    @Column(value = "project_logo")
    private String projectLogo;

    @Column(value = "project_Owner_Id")
    private Integer projectOwnerId;

    private String status;

    @Column(value = "project_desc")
    private String projectDesc;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @SystemColumn(SystemColumnType.CREATE_ON)
    @Column(value = "creation_date")
    private Date creationDate;

    @Column(type=ColumnType.JAVA)
    private String ownerName;

    @Column(type=ColumnType.JAVA)
    private Boolean isOwner;

    @Column(type=ColumnType.JAVA)
    private Integer gtasksNum;

    @Column(type=ColumnType.JAVA)
    private Integer sort;

    @Column(type=ColumnType.JAVA)
    private Boolean isManager;

    @Column(type=ColumnType.JAVA)
    private Boolean isSend;

    @Column(value = "user_id")
    private Integer userId;


    @Column(type=ColumnType.JAVA)
    private Integer sortId;

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public Boolean getSend() {
        return isSend;
    }

    public void setSend(Boolean send) {
        isSend = send;
    }

    public Boolean getManager() {
        return isManager;
    }

    public void setManager(Boolean manager) {
        isManager = manager;
    }

    public Boolean getOwner() {
        return isOwner;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Integer getGtasksNum() {
        return gtasksNum;
    }

    public void setGtasksNum(Integer gtasksNum) {
        this.gtasksNum = gtasksNum;
    }

    public Integer getId() {
        return this.id;
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

    public Integer getProjectOwnerId() {
        return projectOwnerId;
    }

    public void setProjectOwnerId(Integer projectOwnerId) {
        this.projectOwnerId = projectOwnerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}