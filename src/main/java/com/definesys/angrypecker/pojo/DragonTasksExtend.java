package com.definesys.angrypecker.pojo;

import com.definesys.mpaas.query.annotation.Column;
import com.definesys.mpaas.query.annotation.ColumnType;
import com.definesys.mpaas.query.annotation.SQL;
import com.definesys.mpaas.query.annotation.SQLQuery;

import java.util.List;
import java.util.Map;

/**
 * DragonTasks的扩展类
 */
@SQLQuery({
        @SQL(view = "taskuser_v", sql = "select t.*,ut.last_processor,ut.user_id,ut.task_desc,ut.processing_date from dragon_tasks_info t left join dragon_user_task ut on t.id = ut.task_id"),
})
public class DragonTasksExtend extends DragonTasks {

    @Column(type=ColumnType.JAVA)
    private Integer page;

    @Column(type=ColumnType.JAVA)
    private Integer pageSize;

    //export是导出activityProblem活动问题allProblems所有问题
    //myToDo我的待办assignedToMe分配给我myDistribution我的分配
    @Column(type=ColumnType.JAVA)
    private String operation;

    @Column(type=ColumnType.JAVA)
    private String isExport;

    @Column(type=ColumnType.JAVA)
    private String searchMatch;

    /**
     * 项目rowId
     */
    @Column(type=ColumnType.JAVA)
    private String projectRowId;

    @Column(type=ColumnType.JAVA)
    private List<Integer> moduleIds;//模块,可以多个

    //基本信息
    @Column(type=ColumnType.JAVA)
    private List<String> taskTypes;//任务类型
    @Column(type=ColumnType.JAVA)
    private List<String> taskStates;//任务状态
    @Column(type=ColumnType.JAVA)
    private List<String> prioritys;//任务优先级

    //相关人员
    @Column(type=ColumnType.JAVA)
    private List<DragonUser> creators;//创建(分配)人列表
    @Column(type=ColumnType.JAVA)
    private List<DragonUser> approvers;//审批人列表
    @Column(type=ColumnType.JAVA)
    private List<DragonUser> assignees;//处理人列表

    /**
     * 处理任务(task)参数
     *
     */
    private Integer assign;//指派人
//    private Integer approver;//审批人,完成时指派
    private String remarks;//备注
    private List<Map> logResourceUrls;//多文件上传
    private String file;//单文件上传

    //时间范围
    @Column(type=ColumnType.JAVA)
    private TimeCondition creationTimes;//创建时间范围
    @Column(type=ColumnType.JAVA)
    private TimeCondition updateTimes;//修改时间范围
    @Column(type=ColumnType.JAVA)
    private TimeCondition approverTimes;//审核时间范围

    /**
     * 做发邮件设置
     */
    @Column(type=ColumnType.JAVA)
    private DragonTasks myDragonTasks;

    @Column(type=ColumnType.JAVA)
    private TimeCondition endTimes;//截止时间范围

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Integer> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<Integer> moduleIds) {
        this.moduleIds = moduleIds;
    }

    public TimeCondition getCreationTimes() {
        return creationTimes;
    }

    public void setCreationTimes(TimeCondition creationTimes) {
        this.creationTimes = creationTimes;
    }

    public TimeCondition getUpdateTimes() {
        return updateTimes;
    }

    public void setUpdateTimes(TimeCondition updateTimes) {
        this.updateTimes = updateTimes;
    }

    public TimeCondition getApproverTimes() {
        return approverTimes;
    }

    public void setApproverTimes(TimeCondition approverTimes) {
        this.approverTimes = approverTimes;
    }

    public TimeCondition getEndTimes() {
        return endTimes;
    }

    public void setEndTimes(TimeCondition endTimes) {
        this.endTimes = endTimes;
    }

    public List<String> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<String> taskTypes) {
        this.taskTypes = taskTypes;
    }

    public List<String> getTaskStates() {
        return taskStates;
    }

    public void setTaskStates(List<String> taskStates) {
        this.taskStates = taskStates;
    }

    public List<String> getPrioritys() {
        return prioritys;
    }

    public void setPrioritys(List<String> prioritys) {
        this.prioritys = prioritys;
    }

    public String getProjectRowId() {
        return projectRowId;
    }

    public void setProjectRowId(String projectRowId) {
        this.projectRowId = projectRowId;
    }

    public Integer getAssign() {
        return assign;
    }

    public void setAssign(Integer assign) {
        this.assign = assign;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<Map> getLogResourceUrls() {
        return logResourceUrls;
    }

    public void setLogResourceUrls(List<Map> logResourceUrls) {
        this.logResourceUrls = logResourceUrls;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public DragonTasks getMyDragonTasks() {
        return myDragonTasks;
    }

    public void setMyDragonTasks(DragonTasks myDragonTasks) {
        this.myDragonTasks = myDragonTasks;
    }

    public String getIsExport() {
        return isExport;
    }

    public void setIsExport(String isExport) {
        this.isExport = isExport;
    }

    public String getSearchMatch() {
        return searchMatch;
    }

    public void setSearchMatch(String searchMatch) {
        this.searchMatch = searchMatch;
    }

    public List<DragonUser> getCreators() {
        return creators;
    }

    public void setCreators(List<DragonUser> creators) {
        this.creators = creators;
    }

    public List<DragonUser> getApprovers() {
        return approvers;
    }

    public void setApprovers(List<DragonUser> approvers) {
        this.approvers = approvers;
    }

    public List<DragonUser> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<DragonUser> assignees) {
        this.assignees = assignees;
    }
}
