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
 * @Description: 用于展示任务首页的
 * @author: wang
 * @since: 2018-11-19
 * @history: 1.2018-11-19 created by wang
 */
@SQLQuery(value = {//+" dragon_user_task ut on t.user_task_id = ut.id "
    //+" dragon_user_task ut on t.id = ut.task_id "
        @SQL(view = "taskuser_v", sql = TaskUser.SQL_VIEW),
        @SQL(view = "taskuser_v1", sql = TaskUser.SQL_VIEW),
}
)
public class TaskUser extends MpaasBasePojo {

    @Column(type = ColumnType.JAVA)
    static final String SQL_VIEW = "select distinct t.*,\n" +
            "(select u.user_name from fnd_users u where u.id =t.assignee) assigneeCh, \n" +
            "(select u.user_name from fnd_users u where u.id = t.approver)approverCh, (select u.user_name from fnd_users u where u.id = t.creator\n" +
            ")creatorCh from dragon_tasks_info t ";
    /*"select * from (select distinct t.*,ut.last_processor,ut.user_id,ut.task_desc,\n" +
            "ut.processing_date,ut.processing_state, (select u.user_name from fnd_users u where u.id =\n" +
            "if(ut.processing_state = 'approver', ut.last_processor,user_id)) assigneeCh,( if(ut.processing_state\n" +
            " = 'approver', ut.last_processor,user_id)) as assignee, \n" +
            "(select u.user_name from fnd_users u where u.id = t.approver)approverCh, (select u.user_name from fnd_users u where u.id = t.creator\n" +
            ")creatorCh from dragon_tasks_info t \n" +
            "left join dragon_user_task ut on t.user_task_id = ut.id )t ";*/
    /*"select * from (select distinct t.*,ut.last_processor,ut.user_id,ut.task_desc," +
            "ut.processing_date,ut.processing_state, (select u.user_name from fnd_users u where u.id = " +
            "if(ut.processing_state = 'approver', ut.last_processor,user_id)) assigneeCh,( if(ut.processing_state" +
            " = 'approver', ut.last_processor,user_id)) as assignee, (if(ut.processing_state = 'approver'," +
            "ut.user_id,''))approver, (select u.user_name from fnd_users u where u.id = if(ut.processing_state = " +
            "'approver',ut.user_id,''))approverCh, (select u.user_name from fnd_users u where u.id = t.creator" +
            ")creatorCh from dragon_tasks_info t left join ";*/
    private Integer id;

    @Column(value = "project_id")
    private Integer projectId;

    @Column(value = "module_id")
    private Integer moduleId;

    @Column(value = "task_title")
    private String taskTitle;

    @Column(value = "task_type")
    private String taskType;

    @Column(value = "task_state")
    private String taskState;

    @Column(value = "user_task_id")
    private Integer userTaskId;

    private Integer creator;

    private String alt;

    private String priority;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @Column(value = "update_date")
    private Date updateDate;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @Column(value = "planned_start_date")
    private Date plannedStartDate;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @Column(value = "planned_end_date")
    private Date plannedEndDate;

    //工作量
    private Double workload;

    //价值
    private Double worth;

    //环境类型
    @Column(value = "environment_type")
    private String environmentType;

    //版本
    @Column(value = "impact_version")
    private String impactVersion;

    @SystemColumn(SystemColumnType.OBJECT_VERSION)
    @Column(value = "object_version_number")
    private Integer objectVersionNumber;

    @SystemColumn(SystemColumnType.CREATE_BY)
    @Column(value = "created_by")
    private String createdBy;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
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

    //上一个处理人
    @Column(value = "last_processor",type = ColumnType.JAVA)
    private Integer lastProcessor;

    //账户信息Id
    @Column(value = "user_id",type = ColumnType.JAVA)
    private Integer userId;

    //每个处理人的描述
    @Column(value = "task_desc",type = ColumnType.JAVA)
    private String taskDesc;

     /**
     * 处理时间
    */
    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @Column(value = "processing_date",type = ColumnType.JAVA)
    private Date processingDate;

    //处理状态  approver审批人,handler处理人
    @Column(value = "processing_state",type = ColumnType.JAVA)
    private String processingState;


    //处理人
//    @Column(type = ColumnType.JAVA)
    private Integer assignee;



    //审批人
    private Integer approver;

    //处理人中文
    @Column(type = ColumnType.JAVA)
    private String assigneech;

    //审批人中文
    @Column(type = ColumnType.JAVA)
    private String approverch;

    //创建人中文
    @Column(type = ColumnType.JAVA)
    private String creatorch;

    //问题描述
   private String description;


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getModuleId() {
        return this.moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getTaskTitle() {
        return this.taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskType() {
        return this.taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskState() {
        return this.taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public Integer getUserTaskId() {
        return this.userTaskId;
    }

    public void setUserTaskId(Integer userTaskId) {
        this.userTaskId = userTaskId;
    }

    public Integer getCreator() {
        return this.creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getPlannedStartDate() {
        return this.plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Date getPlannedEndDate() {
        return this.plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Double getWorkload() {
        return this.workload;
    }

    public void setWorkload(Double workload) {
        this.workload = workload;
    }

    public Double getWorth() {
        return this.worth;
    }

    public void setWorth(Double worth) {
        this.worth = worth;
    }

    public String getEnvironmentType() {
        return this.environmentType;
    }

    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
    }

    public String getImpactVersion() {
        return this.impactVersion;
    }

    public void setImpactVersion(String impactVersion) {
        this.impactVersion = impactVersion;
    }

    public Integer getObjectVersionNumber() {
        return this.objectVersionNumber;
    }

    public void setObjectVersionNumber(Integer objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Integer getLastProcessor() {
        return this.lastProcessor;
    }

    public void setLastProcessor(Integer lastProcessor) {
        this.lastProcessor = lastProcessor;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTaskDesc() {
        return this.taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public Date getProcessingDate() {
        return this.processingDate;
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
    }

    public String getProcessingState() {
        return this.processingState;
    }

    public void setProcessingState(String processingState) {
        this.processingState = processingState;
    }

    public String getAssigneech() {
        return this.assigneech;
    }

    public void setAssigneech(String assigneech) {
        this.assigneech = assigneech;
    }

    public Integer getAssignee() {
        return this.assignee;
    }

    public void setAssignee(Integer assignee) {
        this.assignee = assignee;
    }

    public Integer getApprover() {
        return this.approver;
    }

    public void setApprover(Integer approver) {
        this.approver = approver;
    }

    public String getApproverch() {
        return this.approverch;
    }

    public void setApproverch(String approverch) {
        this.approverch = approverch;
    }

    public String getCreatorch() {
        return this.creatorch;
    }

    public void setCreatorch(String creatorch) {
        this.creatorch = creatorch;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}