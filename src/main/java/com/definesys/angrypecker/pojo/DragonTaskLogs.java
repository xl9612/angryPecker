package com.definesys.angrypecker.pojo;

import com.definesys.mpaas.query.annotation.*;
import com.definesys.mpaas.query.json.MpaasDateDeserializer;
import com.definesys.mpaas.query.json.MpaasDateSerializer;
import com.definesys.mpaas.query.json.MpaasDateTimeSerializer;
import com.definesys.mpaas.query.model.MpaasBasePojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: wang
 * @since: 2018-11-16
 * @history: 1.2018-11-16 created by wang
 */
@Table(value = "dragon_task_logs")
public class DragonTaskLogs extends MpaasBasePojo {

    @RowID(sequence = "DRAGON_TASK_LOGS_S", type = RowIDType.AUTO)
    private Integer id;

    //对应任务主键
    @Column(value = "task_id")
    private Integer taskId;

    //日志类型：创建、转交、再打开
    private String type;

    //做展示用的,创建的用户名称
    @Column(type = ColumnType.JAVA)
    private String userName;

    //做展示用的,创建日志的用户头像
    @Column(type = ColumnType.JAVA)
    private String userIcon;

    //做展示用的,这个日志的图片
    @Column(type = ColumnType.JAVA)
    private String logResourceUrl;
    //做展示用的,这个日志的图片名称
    @Column(type = ColumnType.JAVA)
    private String logResourceName;


    //做展示用的,这个日志的图片,返回集合给前端
    @Column(type = ColumnType.JAVA)
    private List<Map> logResourceUrls;

    //做展示用的,这个日志的类型->中文
    @Column(type = ColumnType.JAVA)
    private String typech;

    //做展示用的,这个日志的处理人->中文
    @Column(type = ColumnType.JAVA)
    private String assigneech;




    //备注
    private String note;

    //创建人
    private Integer creator;

    private Integer assignee;//记录日志指派人

    @SystemColumn(SystemColumnType.OBJECT_VERSION)
    @Column(value = "object_version_number")
    private Integer objectVersionNumber;

    @SystemColumn(SystemColumnType.CREATE_BY)
    @Column(value = "created_by")
    private String createdBy;

    @JsonSerialize(using = MpaasDateTimeSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @SystemColumn(SystemColumnType.CREATE_ON)
    @Column(value = "creation_date")
    private Date creationDate;

    @SystemColumn(SystemColumnType.LASTUPDATE_BY)
    @Column(value = "last_updated_by")
    private String lastUpdatedBy;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @SystemColumn(SystemColumnType.LASTUPDATE_ON)
    @Column(value = "last_update_date")
    private Date lastUpdateDate;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Integer objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Integer getAssignee() {
        return assignee;
    }

    public void setAssignee(Integer assignee) {
        this.assignee = assignee;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getLogResourceUrl() {
        return logResourceUrl;
    }

    public void setLogResourceUrl(String logResourceUrl) {
        this.logResourceUrl = logResourceUrl;
    }

    public String getTypech() {
        return typech;
    }

    public void setTypech(String typech) {
        this.typech = typech;
    }

    public String getAssigneech() {
        return assigneech;
    }

    public void setAssigneech(String assigneech) {
        this.assigneech = assigneech;
    }

    public List<Map> getLogResourceUrls() {
        return logResourceUrls;
    }

    public void setLogResourceUrls(List<Map> logResourceUrls) {
        this.logResourceUrls = logResourceUrls;
    }

    public String getLogResourceName() {
        return logResourceName;
    }

    public void setLogResourceName(String logResourceName) {
        this.logResourceName = logResourceName;
    }
}