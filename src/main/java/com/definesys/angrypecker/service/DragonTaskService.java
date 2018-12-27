package com.definesys.angrypecker.service;

import com.definesys.angrypecker.pojo.*;
import com.definesys.angrypecker.properties.DragonConstants;
import com.definesys.angrypecker.util.common.DateUtil;
import com.definesys.angrypecker.util.common.DragonStringUtils;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.query.MpaasQuery;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DragonTaskService {

    @Autowired
    private MpaasQueryFactory sw;

    /**
     * 提交任务，完成新任务和操作日志的数据库持久化工作
     * @param sw    倚天工具类
     * @param dragonTasks   任务对象
     */
    @Transactional
    public Integer addTask(MpaasQueryFactory sw, DragonTasks dragonTasks){
        //新建任务时，将任务状态设置为未解决
        dragonTasks.setTaskState("0");
        //设置任务为开启状态
        dragonTasks.setEnabledFlag("TRUE");
        //如果新建任务时没有指定处理人，则将创建人设置为处理人
        if(dragonTasks.getAssignee()==null)
            dragonTasks.setAssignee(dragonTasks.getCreator());
        //如果新建任务时没有指定审批人，则将创建人设置为审批人
        if(dragonTasks.getApprover()==null)
            dragonTasks.setApprover(dragonTasks.getCreator());
        //将任务对象插入数据库，并返回该任务在数据库中的id值
        Object taskId = sw.buildQuery()
                .bind(dragonTasks)
                .doInsert(dragonTasks);
        //添加操作日志
        Integer logId = addTaskLog(sw, Integer.valueOf(taskId.toString()), "ESTABLISH",null, dragonTasks.getCreator(), null);

        return Integer.valueOf(taskId.toString());
    }

    /**
     * 编辑任务
     * @param sw    倚天工具类实例
     * @param dragonTasks   待更新的任务对象
     * @return
     */
    @Transactional
    public DragonTasks updateTask(MpaasQueryFactory sw, DragonTasks dragonTasks){
        //新建一个存储返回结果的map
        Map<String,Object>resultMap = new HashMap<>();
        //修改任务
        sw.buildQuery()
                .bind(dragonTasks)
                .update(new String[]{"task_title","module_id","approver","assignee","alt","task_type","priority","planned_start_date","planned_end_date","workload","worth","environment_type","description"})
                .addRowIdClause("id","=",dragonTasks.getRowId())
                .doUpdate(dragonTasks);
        //添加操作日志
        Integer logId = addTaskLog(sw, dragonTasks.getId(), "MODIFICATIONPROBLEM",null, dragonTasks.getCreator(), dragonTasks.getAssignee());
        //储存日志对象在数据库中的id
        return dragonTasks;
    }

    /**
     * 删除任务,并且删除所有与该任务相关的操作日志及资源记录
     * @param sw    倚天工具类实例
     * @param taskId    任务id
     */
    @Transactional
    public void deleteTask(MpaasQueryFactory sw,Integer taskId){
        sw.buildQuery()
                .bind(DragonTasks.class)
                .addClause("id","=",taskId)
                .doDelete();
        sw.buildQuery()
                .bind(DragonTaskLogs.class)
                .addClause("task_id","=",taskId)
                .doDelete();
        sw.buildQuery()
                .bind(FndResources.class)
                .addClause("task_id","=",taskId)
                .doDelete();
    }

    /**
     * 添加操作日志
     * @param sw 倚天工具类实例
     * @param taskId    任务id
     * @param type      日志类型，对应用户对任务的操作，目前有两种选择，ESTABLISH、MODIFICATION_PROBLEM，分别表示创建和修改
     * @param creator   创建人id
     * @param assignee  当前处理人id
     * @return  返回新建日志的id
     */
    public Integer addTaskLog(MpaasQueryFactory sw,Integer taskId,String type,String note,Integer creator,Integer assignee){
        //给日志对象赋值
        DragonTaskLogs logs = new DragonTaskLogs();
        //设置所属任务的id
        logs.setTaskId(taskId);
        //设置操作类型，如创建、修改。
        logs.setType(type);
        //设置日志注明
        logs.setNote(note);
        //设置创建人
        logs.setCreator(creator);
        //设置当前处理人
        logs.setAssignee(assignee);
        //将日志对象插入数据库
        Object logId = sw.buildQuery()
                .bind(logs)
                .doInsert(logs);
        return Integer.valueOf(logId.toString());
    }
    /**
     * 给taskQueryClaused对象中的timeCondition变量添加开始时间和结束时间
     * @param timeCondition
     */
    public void addTime(TimeCondition timeCondition){
        if(!ValidateUtils.checkIsNull(timeCondition.getTimeOperation())){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME);
            Map<String, Date> timeRange = DateUtil.getTimeRange(timeCondition.getTimeOperation());
            timeCondition.setStartDate(simpleDateFormat.format(timeRange.get("startDate")));
            timeCondition.setEndDate(simpleDateFormat.format(timeRange.get("endDate")));
        }else{
            timeCondition.setStartDate(timeCondition.getStartDate()+" 00:00:00");
            timeCondition.setEndDate(timeCondition.getEndDate()+" 23:59:59");
        }
    }

    public List<Map<String,Object>> getAltsInfo(String alt,Integer projectId){
        if (ValidateUtils.checkIsNull(alt) || ValidateUtils.checkIsNull(projectId+"")){
            return null;
        }
        String sql = "select distinct fu.user_name userName,fu.login_email loginEmail,(select dupi.is_notification from dragon_user_project_info dupi where dupi.user_id = fu.id and dupi.project_id = "+projectId+")isNotification from fnd_users fu ";
        return sw.buildQuery()
                .sql(sql)
                .in("fu.id",getAltsLoginEmail(alt,",",new HashSet()))
                .doQuery();
    }

    //获得所有的Alt人的Id
    public List getAltsLoginEmail(String alt,String regex,HashSet collection){

        collection = (HashSet) DragonStringUtils.strToCollection(alt,regex,null,collection);
        List list = new ArrayList();
        if (ValidateUtils.checkIsNull(collection+"")){
            return null;
        }
        list.addAll(collection);
        return list;
    }

    /**
     * 或取发邮件出去时的任务(task)
     * @param taskId
     * @param message
     * @return
     */
    public DragonTasks getSendEmailTask(Integer taskId,String message){
        //获得详情
        String taskSql = "SELECT ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.assignee ) AS assigneech ,( SELECT u1.login_email FROM fnd_users u1 WHERE u1.id = t.assignee ) AS assigneeEmail ,( SELECT u1.login_email FROM fnd_users u1 WHERE u1.id = t.approver ) AS approverEmail , ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.creator ) AS creatorch , ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.approver ) AS approverch , ( SELECT p.project_name FROM dragon_projects p WHERE p.id = t.project_id ) AS projectName , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 2 AND v.lookup_code = t.task_state AND enabled_flag = 'TRUE') ) AS taskStatech , ( SELECT module_name FROM dragon_project_module_info pm WHERE pm.id = t.module_id ) AS modulech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 3 AND v.lookup_code = t.priority AND enabled_flag = 'TRUE') ) AS prioritych , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 1 AND v.lookup_code = t.task_type AND enabled_flag = 'TRUE') ) AS taskTypech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 5 AND v.lookup_code = t.environment_type AND enabled_flag = 'TRUE') ) AS environmentTypech, t.* FROM dragon_tasks_info t";


        //设值,为发邮件准备,用creatorUserIcon指定是谁指定谁处理
        DragonTasks dragonTask = sw.buildQuery()
                .sql(taskSql)
                .eq("id",taskId)
                .eq("enabled_flag",DragonConstants.DRAGON_ENABLED_TRUE)
                .doQueryFirst(DragonTasks.class);
        if (dragonTask == null){
            return null;
        }
        Integer senderId = 0;
        if ("0".equals(dragonTask.getTaskState())){
            dragonTask.setAssigneeUserIcon(dragonTask.getAssigneech());
            //待解决,发给处理人
            dragonTask.setApproverEmail(dragonTask.getAssigneeEmail());
            dragonTask.setApproverUserIcon(message+"处理");
            //处理者
            dragonTask.setHandler(dragonTask.getAssigneech());
            senderId = dragonTask.getAssignee();
        }else if ("3".equals(dragonTask.getTaskState())){
            //3待审核,发给审核人
            dragonTask.setAssigneeUserIcon(dragonTask.getApproverch());
            dragonTask.setApproverUserIcon(message+"审核");
            dragonTask.setHandler(dragonTask.getApproverch());
            senderId = dragonTask.getApprover();
            //到审核人这处理
        }else {
            dragonTask.setApproverUserIcon("");
        }

        if (!ValidateUtils.checkIsNull(senderId+"")){
            String isNotificationSql = "select distinct dup.is_notification isNotification from dragon_user_project_info dup where dup.user_id = "+senderId+" and dup.project_id ="+dragonTask.getProjectId();
            Map<String,Object> isNotificationMap = sw.buildQuery()
                    .sql(isNotificationSql)
                    .doQueryFirst();

            if (isNotificationMap !=null && "0".equals(isNotificationMap.get("isNotification"))){
                dragonTask.setEnabledFlag(DragonConstants.DRAGON_ENABLED_FALSE);
            }else{
                dragonTask.setEnabledFlag(DragonConstants.DRAGON_ENABLED_TRUE);
            }
        }

        dragonTask = getMyDragonTasks(dragonTask);
        return dragonTask;
    }

    /**
     * 拿到这个任务,并设置
     * @param dragonTask
     * @return
     */
    public DragonTasks getMyDragonTasks(DragonTasks dragonTask){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSEDATE);
        if (dragonTask.getDescription() == null){
            dragonTask.setDescription("");
        }
        //计划开始时间
        if (dragonTask.getPlannedStartDate() != null){
            dragonTask.setPlannedStartDateStr(dateFormat.format(dragonTask.getPlannedStartDate()));
        }else {
            dragonTask.setPlannedStartDateStr("");
        }
        if (dragonTask.getPlannedEndDate() != null){
            dragonTask.setPlannedEndDateStr(dateFormat.format(dragonTask.getPlannedEndDate()));
        }else {
            dragonTask.setPlannedEndDateStr("");
        }
        //环境getWorkload
        if (ValidateUtils.checkIsNull(dragonTask.getEnvironmentTypech())){
            dragonTask.setEnvironmentTypech("");
        }
        //状态
        if (ValidateUtils.checkIsNull(dragonTask.getTaskStatech())){
            dragonTask.setTaskStatech("");
        }
        //描述
        if (ValidateUtils.checkIsNull(dragonTask.getDescription())){
            dragonTask.setDescription("");
        }
        //优先级
        if (ValidateUtils.checkIsNull(dragonTask.getPrioritych())){
            dragonTask.setPrioritych("");
        }


        return dragonTask;
    }
    /**
     * 查询任务时，添加左侧菜单条件
     * @param query
     * @param operation
     * @param userId
     */
    public void addLeftClause(MpaasQuery query,String operation,Integer userId,Integer proId){
        if (DragonConstants.TASK_OPERATION_ACTIVITYPROBLEM.equals(operation)) {
            //0:待解决，3：待审批
            query.in("task_state", "0", "3");
        } else if (DragonConstants.TASK_OPERATION_ALLPROBLEMS.equals(operation)) {
            //所有任务
        } else if (DragonConstants.TASK_OPERATION_MYTODO.equals(operation)) {
            //未解决，当前处理人是我,待审批，当前审批人是我
            query.sql("select * from (select distinct t.*,( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 1 AND v.lookup_code = t.task_type AND enabled_flag = 'TRUE') ) AS taskTypech,( SELECT module_name FROM dragon_project_module_info pm WHERE pm.id = t.module_id ) AS modulech,( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 2 AND v.lookup_code = t.task_state AND enabled_flag = 'TRUE') ) AS taskStatech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 3 AND v.lookup_code = t.priority AND enabled_flag = 'TRUE') ) AS prioritych , (select u.user_name from fnd_users u where u.id =t.assignee) assigneeCh,(select u.user_name from fnd_users u where u.id = t.approver)approverCh, (select u.user_name from fnd_users u where u.id = t.creator)creatorCh from dragon_tasks_info t where (project_id = #proId) and (enabled_flag = 'TRUE') and ((task_state = #firState and assignee = #assignee) or (task_state = #secState and approver = #approver))) tmp")
                    .setVar("firState","0")
                    .setVar("assignee",userId)
                    .setVar("secState","3")
                    .setVar("approver",userId);
        } else if (DragonConstants.TASK_OPERATION_ASSIGNEDTOME.equals(operation)) {
            query.sql("select * from (select distinct t.*,( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 1 AND v.lookup_code = t.task_type AND enabled_flag = 'TRUE') ) AS taskTypech,( SELECT module_name FROM dragon_project_module_info pm WHERE pm.id = t.module_id ) AS modulech,( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 2 AND v.lookup_code = t.task_state AND enabled_flag = 'TRUE') ) AS taskStatech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 3 AND v.lookup_code = t.priority AND enabled_flag = 'TRUE') ) AS prioritych , (select u.user_name from fnd_users u where u.id =t.assignee) assigneeCh,(select u.user_name from fnd_users u where u.id = t.approver)approverCh, (select u.user_name from fnd_users u where u.id = t.creator)creatorCh from dragon_tasks_info t where (project_id = #proId) and (enabled_flag = 'TRUE') and (assignee = #assignee or approver = #approver)) tmp")
                    .setVar("assignee",userId)
                    .setVar("approver",userId);

        } else if (DragonConstants.TASK_OPERATION_MYDISTRIBUTION.equals(operation)) {
            //我的分配（创建人是我）
            query.eq("creator", userId);

        } else if (DragonConstants.TASK_OPERATION_ALTMY.equals(operation)) {
            //@我的
            query.like("alt", String.valueOf(userId));
        }
    }

    public List<Integer> getListProp(List<DragonUser> originList){
        List<Integer>returnList = new ArrayList<>();
        for (DragonUser user:
             originList) {
            if(user.getUserId()!=null&&user.getUserId()>0)
                returnList.add(user.getUserId());
        }
        return returnList;
    }

}
