package com.definesys.angrypecker.pojo;


import com.definesys.mpaas.query.annotation.*;
import com.definesys.mpaas.query.json.MpaasDateDeserializer;
import com.definesys.mpaas.query.json.MpaasDateSerializer;
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
 * @since: 2018-11-13
 * @history: 1.2018-11-13 created by wang
 */
@Table(value = "dragon_tasks_info")
public class DragonTasks extends MpaasBasePojo {

    @Column(type = ColumnType.JAVA)
    private Integer num;

    @RowID(sequence = "dragon_tasks_s", type = RowIDType.AUTO)
    @Style(displayName = "编号",width = "10")
    private Integer id;

    //项目主键
    @Column(value = "project_id")
    @Style(displayName = "项目名")
    private Integer projectId;

    //项目rowId
    @Column(type = ColumnType.JAVA)
    private String proRowId;

    //模块主键
    @Column(value = "module_id")
    private Integer moduleId;

    /**
     * 类型中文,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    @Style(displayName = "类型",width = "10")
    private String taskTypech;

    //标题
    @Column(value = "task_title")
    @Style(displayName = "标题",width = "10")
    private String taskTitle;



    //问题类型（缺陷、改进、任务、需求）
    @Column(value = "task_type")
    @Style(displayName = "类型",width = "10")
    private String taskType;

    //状态（未解决、已延期、已关闭、审批中、已完成）
    @Column(value = "task_state")
    @Style(displayName = "状态",width = "10")
    private String taskState;

    /**
     * 状态中文,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    @Style(displayName = "状态",width = "10")
    private String taskStatech;

    //项目和任务的业务Id
    @Column(value = "task_project_id")
    private Integer taskProjectId;

    private String alt;
    //接受前端的@人的集合
    @Column(type = ColumnType.JAVA)
    private List altId;

    //@人所有Id的集合(List接收/返回前端参数)
    @Column(type = ColumnType.JAVA)
    private List alts;

    //优先级（急、高、中、低）值列表配
    @Column(display = "优先级")
    private String priority;

    /**
     * 优先级中文,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    @Style(displayName = "优先级",width = "10")
    private String prioritych;

    //处理人（从这个项目的人里选）
    @Column(display = "处理人")
    private Integer assignee;

    //分配人（ 创建人）
    @Column(display = "创建人")
    private Integer creator;

    //审核人
    private Integer approver;

    /**
     * 处理人头像,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String assigneeUserIcon;

    /**
     * 处理人登录邮箱,发邮件用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String assigneeEmail;

    /**
     * 审核人登录邮箱,发邮件用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String approverEmail;


    /**
     * 审批人头像,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String approverUserIcon;

    /**
     * 处理人
     */
    @Column(type = ColumnType.JAVA)
    private String handler;

    /**
     * 发送者
     */
    @Column(type = ColumnType.JAVA)
    private String sendler;

    /**
     * 创建人头像,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String creatorUserIcon;

    /**
     * 创建人头像,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String projectName;



    /**
     * 模块中文,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String modulech;



    /**
     * 处理人中文
     */
    @Column(type = ColumnType.JAVA)
    @Style(displayName = "处理人",width = "10")
    private String assigneech;

    /**
     * 审批人中文
     */
    @Column(type = ColumnType.JAVA)
    @Style(displayName = "审批人",width = "10")
    private String approverch;

    /**
     * 创建人中文,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    @Style(displayName = "创建人",width = "10")
    private String creatorch;

    /**
     * 环境中文,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String environmentTypech;

    /**
     * 资源名称String,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String taskResourcesName;

    /**
     * 资源地址String,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private String taskResourcesUrl;

    /**
     * 资源地址List,做查询用
     *
     */
    @Column(type = ColumnType.JAVA)
    private List<Map> taskResourceUrls;


    /**
     * 计划开始时间->字符串显示->发邮件用
     */
    @Column(type = ColumnType.JAVA)
    private String plannedStartDateStr;

    /**
     * 计划截止时间->字符串显示->发邮件用
     */
    @Column(type = ColumnType.JAVA)
    private String plannedEndDateStr;


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

    //
    @Column(value = "enabled_flag")
    private String enabledFlag;

    //问题描述
    private String description;

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
    @Style(displayName = "创建时间",width = "30")
    private Date creationDate;

    //修改时间
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Column(value = "update_date")
    @Style(displayName = "修改时间",width = "20")
    private Date updateDate;

    //计划开始日期
    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @Column(value = "planned_start_date")
    @Style(displayName = "计划时间",width = "30")
    private Date plannedStartDate;

    //计划结束日期
    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @Column(value = "planned_end_date")
    @Style(displayName = "截止时间",width = "30")
    private Date plannedEndDate;

    @SystemColumn(SystemColumnType.LASTUPDATE_BY)
    @Column(value = "last_updated_by")
    private String lastUpdatedBy;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @SystemColumn(SystemColumnType.LASTUPDATE_ON)
    @Column(value = "last_update_date")
    private Date lastUpdateDate;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Date getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Double getWorkload() {
        return workload;
    }

    public void setWorkload(Double workload) {
        this.workload = workload;
    }

    public Double getWorth() {
        return worth;
    }

    public void setWorth(Double worth) {
        this.worth = worth;
    }

    public String getEnvironmentType() {
        return environmentType;
    }

    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
    }

    public String getImpactVersion() {
        return impactVersion;
    }

    public void setImpactVersion(String impactVersion) {
        this.impactVersion = impactVersion;
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

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Integer getApprover() {
        return approver;
    }

    public void setApprover(Integer approver) {
        this.approver = approver;
    }

    public Integer getAssignee() {
        return assignee;
    }
    public void setAssignee(Integer assignee) {
        this.assignee = assignee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssigneech() {
        return assigneech;
    }

    public void setAssigneech(String assigneech) {
        this.assigneech = assigneech;
    }

    public String getApproverch() {
        return approverch;
    }

    public void setApproverch(String approverch) {
        this.approverch = approverch;
    }

    public String getCreatorch() {
        return creatorch;
    }

    public void setCreatorch(String creatorch) {
        this.creatorch = creatorch;
    }

    public String getAssigneeUserIcon() {
        return assigneeUserIcon;
    }

    public void setAssigneeUserIcon(String assigneeUserIcon) {
        this.assigneeUserIcon = assigneeUserIcon;
    }

    public String getApproverUserIcon() {
        return approverUserIcon;
    }

    public void setApproverUserIcon(String approverUserIcon) {
        this.approverUserIcon = approverUserIcon;
    }

    public String getCreatorUserIcon() {
        return creatorUserIcon;
    }

    public void setCreatorUserIcon(String creatorUserIcon) {
        this.creatorUserIcon = creatorUserIcon;
    }

    public String getTaskStatech() {
        return taskStatech;
    }

    public void setTaskStatech(String taskStatech) {
        this.taskStatech = taskStatech;
    }

    public String getModulech() {
        return modulech;
    }

    public void setModulech(String modulech) {
        this.modulech = modulech;
    }

    public String getPrioritych() {
        return prioritych;
    }

    public void setPrioritych(String prioritych) {
        this.prioritych = prioritych;
    }

    public String getTaskTypech() {
        return taskTypech;
    }

    public void setTaskTypech(String taskTypech) {
        this.taskTypech = taskTypech;
    }

    public String getEnvironmentTypech() {
        return environmentTypech;
    }

    public void setEnvironmentTypech(String environmentTypech) {
        this.environmentTypech = environmentTypech;
    }

    public String getTaskResourcesUrl() {
        return taskResourcesUrl;
    }

    public void setTaskResourcesUrl(String taskResourcesUrl) {
        this.taskResourcesUrl = taskResourcesUrl;
    }

    public List<Map> getTaskResourceUrls() {
        return taskResourceUrls;
    }

    public void setTaskResourceUrls(List<Map> taskResourceUrls) {
        this.taskResourceUrls = taskResourceUrls;
    }

    public String getTaskResourcesName() {
        return taskResourcesName;
    }

    public void setTaskResourcesName(String taskResourcesName) {
        this.taskResourcesName = taskResourcesName;
    }

    public String getProRowId() {
        return proRowId;
    }

    public void setProRowId(String proRowId) {
        this.proRowId = proRowId;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public List getAltId() {
        return altId;
    }

    public void setAltId(List altId) {
        this.altId = altId;
    }

    public List getAlts() {
        return alts;
    }

    public void setAlts(List alts) {
        this.alts = alts;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }

    public String getApproverEmail() {
        return approverEmail;
    }

    public void setApproverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
    }

    public String getPlannedStartDateStr() {
        return plannedStartDateStr;
    }

    public void setPlannedStartDateStr(String plannedStartDateStr) {
        this.plannedStartDateStr = plannedStartDateStr;
    }

    public String getPlannedEndDateStr() {
        return plannedEndDateStr;
    }

    public void setPlannedEndDateStr(String plannedEndDateStr) {
        this.plannedEndDateStr = plannedEndDateStr;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getSendler() {
        return sendler;
    }

    public void setSendler(String sendler) {
        this.sendler = sendler;
    }

    public Integer getTaskProjectId() {
        return taskProjectId;
    }

    public void setTaskProjectId(Integer taskProjectId) {
        this.taskProjectId = taskProjectId;
    }
}