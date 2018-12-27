package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.*;
import com.definesys.angrypecker.properties.DragonConstants;
import com.definesys.angrypecker.service.DragonProjectLine;
import com.definesys.angrypecker.service.DragonTaskService;
import com.definesys.angrypecker.service.DragonUserService;
import com.definesys.angrypecker.util.common.DateUtil;
import com.definesys.angrypecker.util.common.DragonStringUtils;
import com.definesys.angrypecker.util.common.ValidateTask;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQuery;
import com.definesys.mpaas.query.MpaasQueryFactory;
import com.definesys.mpaas.query.conf.MpaasQueryConfig;
import com.definesys.mpaas.query.db.PageQueryResult;
import com.definesys.mpaas.query.util.MpaasQueryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.definesys.angrypecker.util.common.ValidateUtils.*;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: wang
 * @since: 2018-11-13
 * @history: 1.2018-11-13 created by wang
 */
@RestController
@RequestMapping(value = "/api/tasks")
public class DragonTasksController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @Autowired
    private DragonProjectLine dragonProjectLine;

    @Autowired
    private DragonUserService dragonUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DragonTaskService dragonTaskService;

    @Autowired
    private MpaasQueryConfig config;

    @PostMapping(value = "/getAllTask")
    public Response getTasksByClauses(@RequestBody DragonTasksExtend taskQueryClauses, HttpServletResponse servletResponse) {
        if (taskQueryClauses == null)
            throw new MpaasBusinessException("请填写检索条件");
        if (ValidateUtils.checkIsNull(taskQueryClauses.getProjectRowId()))
            throw new MpaasBusinessException("项目rowId不存在！");
        //response的data内容
        Map<String, Object> resultMap = new HashMap<>();
        //获取前端传过来的左侧菜单task数据列表
        List<Map<String, Object>> leftSizeList = new ArrayList<>();
        //获取前端的operation参数
        String operation = taskQueryClauses.getOperation();
        //约定operation不能为空，当用户没有选择时，默认为"活动任务",否则抛出异常。
        if (ValidateUtils.checkIsNull(operation))
            throw new MpaasBusinessException("未知的操作，比如我的代办、分配给我？");
        //对proRowId解密
        String proIdStr = MpaasQueryUtil.decryptRowId(taskQueryClauses.getProjectRowId().trim(), config.rowIdSecret);
        //将String类型的proIdStr转化为Integer类型的proId
        Integer proId = Integer.valueOf(proIdStr);
        //获取当前登陆用户及id
        DragonUser dragonUser = dragonUserService.getDragonUser();
        Integer userId = dragonUser.getId();
        //查看值列表（左侧菜单）
        List<Map<String, Object>> lookUpValuesMap = sw.buildQuery()
                .sql("select lookup_code code,meaning name from fnd_lookup_values")
                .eq("lookup_id", 7)
                .doQuery();
        //活动任务
        Map<String, Object> activityTasks = sw.buildQuery()
                .sql("SELECT COUNT(*) size FROM dragon_tasks_info")
                .in("task_state", "0", "3")
                .eq("project_id", proId)
                .doQueryFirst();
        leftSizeList.add(activityTasks);
        //所有任务
        Map<String, Object> allTasks = new HashMap<>();
        allTasks.put("size",0);
        leftSizeList.add(allTasks);
        //我的待办
        Map<String, Object> myToDo = sw.buildQuery()
                .sql("SELECT COUNT(*) size FROM dragon_tasks_info where ((task_state = #state and assignee = #assignee) or (task_state = #mystate and approver = #approver)) and project_id = #proId")
                .setVar("state", "0")
                .setVar("assignee", userId)
                .setVar("mystate", "3")
                .setVar("approver", userId)
                .setVar("proId", proId)
                .doQueryFirst();
        leftSizeList.add(myToDo);
        //分配给我，当前处理人是我，或者审批人是我
        Map<String,Object> assignToMe = new HashMap<>();
        assignToMe.put("size",0);
        leftSizeList.add(assignToMe);
        //我的分配
        Map<String,Object> myDistribution = new HashMap<>();
        myDistribution.put("size",0);
        leftSizeList.add(myDistribution);
        //@我的
        Map<String, Object> altMe = sw.buildQuery()
                .sql("SELECT COUNT(*) size FROM dragon_tasks_info")
                .eq("project_id", proId)
                .like("alt", String.valueOf(userId))
                .groupBegin()
                .conjuctionAnd()
                .eq("task_state","0")
                .conjuctionOr()
                .eq("task_state","3")
                .groupEnd()
                .doQueryFirst();
        leftSizeList.add(altMe);
        //添加条件proId
        MpaasQuery query = sw.buildQuery()
                .sql("select * from (select distinct t.*,( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 1 AND v.lookup_code = t.task_type AND enabled_flag = 'TRUE') ) AS taskTypech,( SELECT module_name FROM dragon_project_module_info pm WHERE pm.id = t.module_id ) AS modulech,( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 2 AND v.lookup_code = t.task_state AND enabled_flag = 'TRUE') ) AS taskStatech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 3 AND v.lookup_code = t.priority AND enabled_flag = 'TRUE') ) AS prioritych , (select u.user_name from fnd_users u where u.id =t.assignee) assigneeCh,(select u.user_name from fnd_users u where u.id = t.approver)approverCh, (select u.user_name from fnd_users u where u.id = t.creator)creatorCh from dragon_tasks_info t where (project_id = #proId) and (enabled_flag = 'TRUE')) tmp");
        //左侧条件不为空
        if (!ValidateUtils.checkIsNull(operation)) {
            dragonTaskService.addLeftClause(query, operation, userId,proId);
        }
        //添加模块条件
        if (!checkIsCollectionNull(taskQueryClauses.getModuleIds())) {
            query.in("module_id", taskQueryClauses.getModuleIds());
        }
        //添加问题类型条件
        if (!checkIsCollectionNull(taskQueryClauses.getTaskTypes())) {
            query.in("task_type", taskQueryClauses.getTaskTypes());
        }
        //添加问题状态条件
        if (!checkIsCollectionNull(taskQueryClauses.getTaskStates())) {
            query.in("task_state", taskQueryClauses.getTaskStates());
        }
        //添加优先级条件
        if (!checkIsCollectionNull(taskQueryClauses.getPrioritys())) {
            query.in("priority", taskQueryClauses.getPrioritys());
        }
        //添加创建人条件
        if (!checkIsCollectionNull(taskQueryClauses.getCreators())) {
            List<DragonUser> creators = taskQueryClauses.getCreators();
            List<Integer> listProp = dragonTaskService.getListProp(creators);
            query.in("creator", listProp);
        }
        //添加处理人条件
        if (!checkIsCollectionNull(taskQueryClauses.getAssignees())) {
            List<DragonUser> assignees = taskQueryClauses.getAssignees();
            List<Integer> listProp = dragonTaskService.getListProp(assignees);
            query.in("assignee", listProp);
        }
        //添加审核人条件
        if (!checkIsCollectionNull(taskQueryClauses.getApprovers())) {
            List<DragonUser> approvers = taskQueryClauses.getApprovers();
            List<Integer> listProp = dragonTaskService.getListProp(approvers);
            query.in("approver", listProp);
        }
        //添加创建日期条件
        TimeCondition creationTimes = taskQueryClauses.getCreationTimes();
        if (creationTimes!=null&&!checkIsTimeConditionNull(creationTimes)) {
            dragonTaskService.addTime(creationTimes);
            query.gteq("creation_date", creationTimes.getStartDate())
                    .lteq("creation_date", creationTimes.getEndDate());
        }
        //添加修复日期条件,已完成，待审核
        TimeCondition updateTimes = taskQueryClauses.getUpdateTimes();
        if (creationTimes!=null&&!checkIsTimeConditionNull(updateTimes)) {
            dragonTaskService.addTime(updateTimes);
            query.in("task_state", "3", "4")
                    .gteq("update_date", updateTimes.getStartDate())
                    .lteq("update_date", updateTimes.getEndDate());
        }
        //添加审核日期，已解决
        TimeCondition approveTimes = taskQueryClauses.getApproverTimes();
        if (creationTimes!=null&&!checkIsTimeConditionNull(approveTimes)) {
            dragonTaskService.addTime(approveTimes);
            query.eq("task_state", "4")
                    .gteq("update_date", approveTimes.getStartDate())
                    .lteq("update_date", approveTimes.getEndDate());
        }
        //添加计划截至日期条件
        TimeCondition endTimes = taskQueryClauses.getEndTimes();
        if (creationTimes!=null&&!checkIsTimeConditionNull(endTimes)) {
            dragonTaskService.addTime(endTimes);
            query.addClause("task_state", "!=", "4")
                    .gteq("planned_end_date", endTimes.getStartDate())
                    .lteq("planned_end_date", endTimes.getEndDate());
        }
        //添加搜索框条件
        if (!checkIsNull(taskQueryClauses.getSearchMatch())) {
            query.like("task_title", taskQueryClauses.getSearchMatch());
        }
        //添加分页条件
        if (taskQueryClauses.getPage() == null || taskQueryClauses.getPage() <= 0) {
            taskQueryClauses.setPage(1);
        }
        if (taskQueryClauses.getPageSize() == null || taskQueryClauses.getPageSize() <= 0) {
            taskQueryClauses.setPageSize(10);
        }
        query.setVar("proId",proId);
        //判断是查询还是导出操作,“1”代表导出，“0”代表查询
        String isExport = taskQueryClauses.getIsExport();
        if (isExport != null && isExport.equals("1")) {
            query.fileName("allTask.xlsx")
                    .include("id","taskTypech","taskTitle","taskTypech","taskStatech","prioritych","assigneeCh","approverCh","creatorch","plannedStartDate","plannedEndDate","creationDate","updateDate")
                    .doExport(servletResponse, DragonTasks.class);
            return Response.ok();
        } else {
            PageQueryResult<DragonTasks> dragonTasksPageQueryResult = query.orderBy("task_project_id", "desc")
                    .doPageQuery(taskQueryClauses.getPage(), taskQueryClauses.getPageSize(), DragonTasks.class);
            if (!checkIsCollectionNull(lookUpValuesMap) && !checkIsCollectionNull(leftSizeList)) {
                //遍历数据库中左侧菜单直列表list
                for (int i = 0; i < lookUpValuesMap.size(); i++) {
                    //如果与当前操作一致，比如当前用户点击”我的代办“，则将”我的代办“任务数目修改为数据库
                    Object code = lookUpValuesMap.get(i).get("code");
                    boolean equalsOperation = code.equals(operation);
                    boolean equalsAllProblems = operation.equals(DragonConstants.TASK_OPERATION_ALLPROBLEMS);
                    boolean equalsAssignToMe = operation.equals(DragonConstants.TASK_OPERATION_ASSIGNEDTOME);
                    boolean equalsMyDistribution = operation.equals(DragonConstants.TASK_OPERATION_MYDISTRIBUTION);
                    if (equalsOperation&&!equalsAllProblems&&!equalsAssignToMe&&!equalsMyDistribution) {
                        lookUpValuesMap.get(i).put("size", dragonTasksPageQueryResult.getCount());
                        continue;
                    }
                    lookUpValuesMap.get(i).put("size", leftSizeList.get(i).get("size"));
                }

                if(dragonProjectLine.isManagerRoleForPro(proId,dragonUser.getId(),"")){
                    Map<String, Object> chart = new HashMap<>();
                    chart.put("code","taskOverlord");
                    chart.put("name","任务统计");
                    chart.put("size",0);
                    lookUpValuesMap.add(chart);
                }
            }
            resultMap.put("tasks", dragonTasksPageQueryResult);
            resultMap.put("leftMenuList", lookUpValuesMap);
            return Response.ok().data(resultMap);
        }
    }


    /**
     * 提交任务
     * @param dragonTasks
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response addDragonTasks(@RequestBody DragonTasks dragonTasks) {
        //获取前端传递的任务对象
        if(dragonTasks==null)
            throw new MpaasBusinessException("新建任务不能为空");
        //判断该任务所在项目和模块是否存在
        if(dragonTasks.getProRowId()==null||"".equals(dragonTasks.getProRowId()))
            throw new MpaasBusinessException("该任务所在项目不存在");
        if(dragonTasks.getModuleId()==null||dragonTasks.getModuleId()<=0)
            throw new MpaasBusinessException("该任务所在模块不存在");
        //设置任务创建人（根据当前登录人）
        DragonUser dragonUser = dragonUserService.getDragonUser();
        dragonTasks.setCreator(dragonUser.getId());
        //设置修改时间（当前时间）
        dragonTasks.setUpdateDate(new Date());
        //给项目rowid解密
        String proIdStr = MpaasQueryUtil.decryptRowId(dragonTasks.getProRowId(), config.rowIdSecret);
        Integer proId = null;
        try{
            proId = Integer.valueOf(proIdStr);
        }catch (NumberFormatException e){
            throw new MpaasBusinessException("proRowid有误!");
        }
        dragonTasks.setProjectId(proId);
        //获取当前项目所有任务的总数
        Map<String, Object> maxValue = sw.buildQuery()
                .sql("SELECT MAX(ifNull(task_project_id,0))+1 as value FROM dragon_tasks_info")
                .eq("project_id", proId)
                .doQueryFirst();
        Object maxValueId = maxValue.get("value");
        //设置该任务的所在项目的序列号，为什么你提交不了
        dragonTasks.setTaskProjectId(Integer.parseInt(maxValueId.toString()));
        //设置@的人
        dragonTasks.setAlt(DragonStringUtils.collectionToString(dragonTasks.getAltId(),","));
        Integer taskId = dragonTaskService.addTask(sw, dragonTasks);
        dragonTasks.setId(taskId);
        //发邮件
        String msg = "提交了任务,需要您";

        //查询最新的任务信息
        //设值,发邮件需要
        dragonTasks.setCreatorUserIcon(dragonUser.getUserName());
        dragonTasks.setSendler(dragonUser.getUserName());
        dragonTasks.setModulech(msg);

        //获取前端上传的文件路径
        List<Map> taskResourceUrls = dragonTasks.getTaskResourceUrls();
        if(!ValidateUtils.checkIsCollectionNull(taskResourceUrls)){
            Map map = DragonStringUtils.listToString(taskResourceUrls);
            FndResources fndResources = new FndResources();
            fndResources.setResourceName(map.get("usernames").toString());
            fndResources.setResourceUrl(map.get("urls").toString());
            //设置所属任务id
            fndResources.setTaskId(taskId);
            //设置是否开启
            fndResources.setEnabledFlag("true");
            Integer resId= addFndResource(sw, fndResources);
        }
        return Response.ok().data(taskId).setMessage("操作成功");
    }

    /**
     * 编辑任务
     * @param dragonTasks
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response updateDragonTasks(@RequestBody DragonTasks dragonTasks) {
        //获取前端的任务对象
        if (dragonTasks == null)
            return Response.error("任务不能为空");
        if (ValidateUtils.checkIsNull(dragonTasks.getModuleId()+""))
            return Response.error("该任务所在模块不存在");
        if (dragonTasks.getRowId() == null || "".equals(dragonTasks.getRowId()))
            return Response.error("rowId不能为空");
        DragonUser dragonUser = dragonUserService.getDragonUser();
        dragonTasks.setId(Integer.valueOf(MpaasQueryUtil.decryptRowId(dragonTasks.getRowId(),config.rowIdSecret)));
        Integer tasksId = dragonTasks.getId();
        dragonTasks.setUpdateDate(new Date());

        dragonTasks.setCreator(dragonUser.getId());
        dragonTasks.setHandler(dragonUser.getUserName());
        dragonTasks.setCreatorUserIcon(dragonUser.getUserName());
        String updateMsg = "更新了任务,需要您";

        dragonTasks.setAlt(DragonStringUtils.collectionToString(dragonTasks.getAltId(),","));
        //设值,发邮件需要
        dragonTasks.setCreatorUserIcon(dragonUser.getUserName());
        dragonTasks.setSendler(dragonUser.getUserName());
        dragonTasks.setModulech(updateMsg);

        List<Map> taskResourceUrls = dragonTasks.getTaskResourceUrls();
            //查询该任务是否存在与之对应的fnd_resource记录
            FndResources fndResources = sw.buildQuery()
                    .sql("select * from fnd_resources")
                    .eq("task_id", tasksId)
                    .doQueryFirst(FndResources.class);
            //如果不存在
            if (fndResources == null) {
                //新建一个FndResource对象
                if(!checkIsCollectionNull(taskResourceUrls)){
                    fndResources = new FndResources();
                    Map map2 = DragonStringUtils.listToString(taskResourceUrls);
                    fndResources.setResourceName(map2.get("usernames").toString());
                    fndResources.setResourceUrl(map2.get("urls").toString());
                    //设置所属任务id
                    fndResources.setTaskId(tasksId);
                    //设置是否开启
                    fndResources.setEnabledFlag("true");
                    addFndResource(sw, fndResources);
                }
            } else {
                if(!checkIsCollectionNull(taskResourceUrls)){
                    Map map1 = DragonStringUtils.listToString(taskResourceUrls);
                    fndResources.setResourceName(map1.get("usernames").toString());
                    fndResources.setResourceUrl(map1.get("urls").toString());
                }else{
                    fndResources.setResourceName(null);
                    fndResources.setResourceUrl(null);
                }
                updateFndResource(sw, fndResources);
            }

        //更新任务并添加日志
        dragonTaskService.updateTask(sw, dragonTasks);
        return Response.ok().setMessage("更新成功");
    }

    /**
     * 删除任务,并且删除所有与该任务相关的操作日志及资源记录
     *
     * @param map 任务的唯一标识
     * @return
     */
    @PostMapping(value = "/delete")
    public Response deleteDragonTasks(@RequestBody Map<String, String> map) {
        String rowId = map.get("rowId");
        DragonTasks tasks = sw.buildQuery()
                .addRowIdClause("id", "=", rowId)
                .doQueryFirst(DragonTasks.class);
        if (tasks == null)
            throw new MpaasBusinessException("该任务不存在");
        dragonTaskService.deleteTask(sw, tasks.getId());
        return Response.ok().setMessage("操作成功");
    }

    /**
     * 添加一条fndResource记录
     *
     * @param sw           倚天工具类实例
     * @param fndResources 资源类对象
     * @return 返回资源记录在数据库中的id
     */
    private Integer addFndResource(MpaasQueryFactory sw, FndResources fndResources) {
        Object resId = sw.buildQuery()
                .bind(fndResources)
                .doInsert(fndResources);
        return Integer.valueOf(resId.toString());
    }

    private void updateFndResource(MpaasQueryFactory sw, FndResources fndResources) {
        sw.buildQuery()
                .bind(fndResources)
                .update(new String[]{"resource_name", "resource_url"})
                .doUpdate(fndResources);
    }

    /**
     * 返回任务主页->左侧显示
     * @return
     */
    @RequestMapping(value = "/getOperationTaskSize", method = RequestMethod.POST)
    public Response getOperationTaskSize(@RequestBody Map map){
        String projectRowId = (String)map.get("projectRowId");

        if (ValidateUtils.checkIsNull(projectRowId)){
            return Response.error("项目RowId不能为空");
        }

        DragonProjects p = sw.buildQuery()
                .addRowIdClause("id","=",projectRowId)
                .doQueryFirst(DragonProjects.class);

        //要不要判断登录人是否在这个项目下

        if (ValidateUtils.checkIsNull(p+"")){
            return Response.error("该项目不存在,请从新选择项目");
        }

        DragonUser dragonUser = dragonUserService.getDragonUser();

        //判断当前登录人是否在这个项目下
       /* if (!dragonProjectLine.projectMemberIsExisteCurrLogin(dragonUser.getId(),p.getId())){
            return Response.error("你没有查看这个项目任务的权限");
        }*/
        String altMy = "select distinct alt from dragon_tasks_info";
        List<Map<String,Object>> altMys = sw.buildQuery()
                .sql(altMy)
                .eq("project_id",p.getId())
                .doQuery();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map map1 : altMys){
            if (!ValidateUtils.checkIsNull(map1+"")){
                stringBuilder.append(map1.get("alt")+",");
            }
        }
       List list = null;

        list = (ArrayList)DragonStringUtils.strToCollection(stringBuilder.toString(),",",dragonUser.getId()+"",list);

        String myToDoSql = "(SELECT COUNT(*) FROM dragon_tasks_info WHERE project_id = "+p.getId()+" AND ((task_state = 0 AND assignee = "+dragonUser.getId()+") OR (task_state = 3 AND approver = "+dragonUser.getId()+")))";
       //有个条件lookup_id=7写死了
        if (list == null){
            list = new ArrayList();
        }
        String sql = "SELECT v.lookup_code AS code, v.meaning AS name , CASE v.lookup_code WHEN 'activityProblem' THEN ( SELECT COUNT(*) FROM dragon_tasks_info WHERE task_state IN (0, 3) and project_id = "+p.getId()+" ) WHEN 'myToDo' THEN "+myToDoSql+" WHEN 'altMy' THEN "+list.size()+" ELSE 0 END AS size FROM fnd_lookup_values v WHERE v.enabled_flag = 'TRUE' AND v.lookup_id = 7";

        List<Map<String, Object>> maps = sw.buildQuery().sql(sql).doQuery();

        if (dragonProjectLine.isManagerRoleForPro(p.getId(),dragonUser.getId(),"")){
            Map manager = new HashMap();
            manager.put("code","taskOverlord");
            manager.put("name","任务统计");
            manager.put("size",0);
            maps.add(manager);
        }

        return Response.ok().setData(maps);

    }

    /**
     * 获取这个任务(task)详情
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/getTaskById", method = RequestMethod.POST)
    public Response getTaskById(@RequestBody Map map) {

        String taskRowId = (String)map.get("rowId");
        if (ValidateUtils.checkIsNull(taskRowId)) {
            return Response.error("任务Id不能为空");
        }
        String taskSql = "SELECT ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.assignee ) AS assigneech , ( SELECT u1.user_icon FROM fnd_users u1 WHERE u1.id = t.assignee ) AS assigneeUserIcon , ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.creator ) AS creatorch , ( SELECT u1.user_icon FROM fnd_users u1 WHERE u1.id = t.creator ) AS creatorUserIcon , ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.approver ) AS approverch , ( SELECT u1.user_icon FROM fnd_users u1 WHERE u1.id = t.approver ) AS approverUserIcon , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 2 AND v.lookup_code = t.task_state AND enabled_flag = 'TRUE') ) AS taskStatech , ( SELECT module_name FROM dragon_project_module_info pm WHERE pm.id = t.module_id ) AS modulech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 3 AND v.lookup_code = t.priority AND enabled_flag = 'TRUE') ) AS prioritych , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 1 AND v.lookup_code = t.task_type AND enabled_flag = 'TRUE') ) AS taskTypech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 5 AND v.lookup_code = t.environment_type AND enabled_flag = 'TRUE') ) AS environmentTypech , ( SELECT r.resource_url FROM fnd_resources r WHERE r.task_id = t.id AND r.enabled_flag = 'TRUE' ) AS taskResourcesUrl,( SELECT r.resource_name FROM fnd_resources r WHERE r.task_id = t.id AND r.enabled_flag = 'TRUE' ) AS taskResourcesName, t.* FROM dragon_tasks_info t";

        DragonTasks dragonTask = sw.buildQuery()
                .sql(taskSql)
                .addRowIdClause("id","=",taskRowId)
                .eq("enabled_flag",DragonConstants.DRAGON_ENABLED_TRUE)
                .doQueryFirst(DragonTasks.class);

        if (ValidateUtils.checkIsNull(dragonTask+"")){
            return Response.error("任务不存在");
        }

        if (!ValidateUtils.checkIsNull(dragonTask.getTaskResourcesUrl())){
           //dragonTask.setTaskResourceUrls(Arrays.asList(dragonTask.getTaskResourcesUrl().split(",")));
            dragonTask.setTaskResourceUrls(DragonStringUtils.stringToList(dragonTask.getTaskResourcesName(),
                    dragonTask.getTaskResourcesUrl()));
        }

        if (!ValidateUtils.checkIsNull(dragonTask.getAlt())){
            Set set = new HashSet();
            set = (HashSet)DragonStringUtils.strToCollection(dragonTask.getAlt(),",",null,set);
            List list = new ArrayList();
            list.addAll(set);
            List<Map<String,Object>> users = sw.buildQuery()
                    .sql("select id,user_name userName from fnd_users")
                    .in("id",list)
                    .doQuery();

            list = new ArrayList(users.size());
            List altIds = new ArrayList(users.size());
            for (int i = 0; i < users.size(); i++) {
                list.add(users.get(i).get("userName"));
                altIds.add(users.get(i).get("id"));
            }
            dragonTask.setAltId(altIds);
            dragonTask.setAlts(list);
        }

        String sql = "SELECT u.user_name AS userName, u.user_icon AS userIcon ,tl.id,tl.type,tl.assignee,tl.creator, ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 4 AND v.lookup_code = tl.type AND enabled_flag = 'TRUE') ) AS typech, tl.note , ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = tl.assignee ) AS assigneech, tl.creation_date AS creationDate , ( SELECT r.resource_url FROM fnd_resources r WHERE r.task_log_id = tl.id AND enabled_flag = 'TRUE' ) AS logResourceUrl,( SELECT r.resource_name FROM fnd_resources r WHERE r.task_log_id = tl.id AND enabled_flag = 'TRUE' ) AS logResourceName FROM dragon_task_logs tl LEFT JOIN fnd_users u ON tl.creator = u.id";
        List<DragonTaskLogs> dragonTaskLogs = sw.buildQuery()
                .sql(sql)
                .eq("task_id",dragonTask.getId())
                .orderBy("id","desc")
                .doQuery(DragonTaskLogs.class);
        for (DragonTaskLogs taskLog : dragonTaskLogs){
            if (!ValidateUtils.checkIsNull(taskLog.getLogResourceUrl())){
                //taskLog.setLogResourceUrls(Arrays.asList(dragonTask.getTaskResourcesUrl().split(",")));
                taskLog.setLogResourceUrls(DragonStringUtils.stringToList(taskLog.getLogResourceName(),
                        taskLog.getLogResourceUrl()));
            }
        }
        DragonUser dragonUser = dragonUserService.getDragonUser();


        Map data = new HashMap();
        boolean isManagerRoleForPro = dragonProjectLine.isManagerRoleForPro(dragonTask.getProjectId(), dragonUser.getId(), "");
        if (isManagerRoleForPro || dragonUser.getId() == dragonTask.getCreator()){
            data.put("updateAndDelte","TRUE");
        }
        //显示的按钮
        List<String> allOperations = ValidateTask.getAllOperations(dragonTask.getTaskState(), dragonTask.getCreator(), dragonTask.getAssignee(),
                dragonTask.getApprover(), dragonUser.getId(),isManagerRoleForPro);

        data.put("allOperations",allOperations);
        data.put("dragonTask",dragonTask);
        data.put("logs",dragonTaskLogs);

        return Response.ok().setData(data);
    }

    /**操作任务
     * String taskState;//设置任务状态(task)
     *标识,指派:修改task信息中的user_task_id为最新的这个userTask(的user_id为item.getAssign()),
     * 完成:修改tas信息中的approver(审核人),为传入的这个item.getApprover()
     * @param item
     * @return
     */
    @RequestMapping(value = "/operationTask", method = RequestMethod.POST)
    @Transactional
    public Response getOperationTask(@RequestBody DragonTasksExtend item) {
        String taskRowId = item.getRowId();
        if (ValidateUtils.checkIsNull(taskRowId)) {
            item.setEnabledFlag(DragonConstants.DRAGON_ENABLED_FALSE);
            return Response.error("任务Id不能为空");
        }
        if (ValidateUtils.checkIsNull(item.getOperation())) {
            item.setEnabledFlag(DragonConstants.DRAGON_ENABLED_FALSE);
            return Response.error("请输入具体的操作");
        }

        //获得详情
        //String taskSql = "SELECT ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.assignee ) AS assigneech ,( SELECT u1.login_email FROM fnd_users u1 WHERE u1.id = t.assignee ) AS assigneeEmail ,( SELECT u1.login_email FROM fnd_users u1 WHERE u1.id = t.approver ) AS approverEmail , ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.creator ) AS creatorch , ( SELECT u1.user_name FROM fnd_users u1 WHERE u1.id = t.approver ) AS approverch , ( SELECT p.project_name FROM dragon_projects p WHERE p.id = t.project_id ) AS projectName , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 2 AND v.lookup_code = t.task_state AND enabled_flag = 'TRUE') ) AS taskStatech , ( SELECT module_name FROM dragon_project_module_info pm WHERE pm.id = t.module_id ) AS modulech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 3 AND v.lookup_code = t.priority AND enabled_flag = 'TRUE') ) AS prioritych , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 1 AND v.lookup_code = t.task_type AND enabled_flag = 'TRUE') ) AS taskTypech , ( SELECT meaning FROM fnd_lookup_values v WHERE (v.lookup_id = 5 AND v.lookup_code = t.environment_type AND enabled_flag = 'TRUE') ) AS environmentTypech, t.* FROM dragon_tasks_info t";
        DragonTasks dragonTask = sw.buildQuery()
                .view("taskuser_v")
                .addRowIdClause("id","=",taskRowId)
                .eq("enabled_flag",DragonConstants.DRAGON_ENABLED_TRUE)
                .doQueryFirst(DragonTasks.class);
        if (ValidateUtils.checkIsNull(dragonTask + "")) {
            item.setEnabledFlag(DragonConstants.DRAGON_ENABLED_FALSE);
            return Response.error("没有该任务");
        }
        DragonUser dragonUser = dragonUserService.getDragonUser();


        item.setCreator(dragonUser.getId());
        DragonTaskLogs taskLogs = new DragonTaskLogs();
        taskLogs.setTaskId(dragonTask.getId());
        taskLogs.setCreator(dragonUser.getId());
        taskLogs.setNote(item.getRemarks());//描述
        String taskState = dragonTask.getTaskState();

        String message = "";//中间显示的消息
        if (DragonConstants.TASK_HANDLER_OPERATION_ASSIGN.equals(item.getOperation())) {
            //指派

            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
                return Response.error("该任务不能执行该操作:"+item.getOperation());
            }

            if (!dragonProjectLine.isManagerRoleForPro(dragonTask.getProjectId(), dragonUser.getId(), "") &&
                    dragonTask.getCreator() != dragonUser.getId() &&
                    dragonTask.getApprover() != dragonUser.getId() &&
                    dragonTask.getAssignee() != dragonUser.getId()
                    ) {
                return Response.error("你没有指派这个任务的权限");
            }

            if (ValidateUtils.checkIsNull(item.getAssign() + "")) {
                return Response.error("指派人不能为空");
            }
            item.setEnabledFlag(DragonConstants.DRAGON_ENABLED_TRUE);
            message = "指派给您";
            dragonTask.setAssignee(item.getAssign());//指定处理人
            dragonTask.setTaskState(DragonConstants.TASK_STATE_HANDLER);
            taskLogs.setAssignee(item.getAssign());
            taskLogs.setType(DragonConstants.TASK_HANDLER_OPERATION_ASSIGN.toUpperCase());
        } else if (DragonConstants.TASK_HANDLER_OPERATION_DELAY.equals(item.getOperation())) {
            //延期

            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
                return Response.error("该任务不能执行该操作:"+item.getOperation());
            }

            if (!dragonProjectLine.isManagerRoleForPro(dragonTask.getProjectId(), dragonUser.getId(), "") &&
                    dragonTask.getCreator() != dragonUser.getId() &&
                    dragonTask.getApprover() != dragonUser.getId()) {
                return Response.error("你没有延期这个任务的权限");
            }
            //修改任务(task)的状态为延期
            dragonTask.setTaskState("1");
            taskLogs.setType(DragonConstants.TASK_HANDLER_OPERATION_DELAY.toUpperCase());

        } else if (DragonConstants.TASK_HANDLER_OPERATION_CLOSE.equals(item.getOperation())) {
            //关闭

            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
                return Response.error("该任务不能执行该操作:"+item.getOperation());
            }

            if (!dragonProjectLine.isManagerRoleForPro(dragonTask.getProjectId(), dragonUser.getId(), "") &&
                    dragonTask.getCreator() != dragonUser.getId()) {
                return Response.error("你没有关闭这个任务的权限");
            }

            dragonTask.setTaskState("2");
            taskLogs.setType(DragonConstants.TASK_HANDLER_OPERATION_CLOSE.toUpperCase());

        } else if (DragonConstants.TASK_HANDLER_OPERATION_OPEN_AGAIN.equals(item.getOperation())) {
            //再打开

            if (!"1".equals(taskState) && !"2".equals(taskState) && !"4".equals(taskState)){

                return Response.error("该任务不能执行再打开");
            }

            if (!dragonProjectLine.isManagerRoleForPro(dragonTask.getProjectId(), dragonUser.getId(), "") &&
                    dragonTask.getCreator() != dragonUser.getId()) {

                return Response.error("你没有再打开这个任务的权限");
            }
            item.setEnabledFlag(DragonConstants.DRAGON_ENABLED_TRUE);
            message = "再打开,指派给您";
            //再打开状态回到待解决
            dragonTask.setTaskState(DragonConstants.TASK_STATE_HANDLER);
            taskLogs.setType(DragonConstants.TASK_HANDLER_OPERATION_OPEN_AGAIN.toUpperCase());
        } else if (DragonConstants.TASK_HANDLER_OPERATION_COMPLETE.equals(item.getOperation())) {
            //完成:点完成就是指定审批人

            //只有待解决才能完成
            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState) || "3".equals(taskState)){

                return Response.error("该任务不能执行该操作:"+item.getOperation());
            }

            if (!dragonProjectLine.isManagerRoleForPro(dragonTask.getProjectId(), dragonUser.getId(), "") &&
                    dragonTask.getCreator() != dragonUser.getId() &&
                    dragonTask.getAssignee() != dragonUser.getId()
                    ) {

                return Response.error("你没有完成这个任务的权限");
            }
            if (ValidateUtils.checkIsNull(item.getApprover() + "")) {

                return Response.error("审核人不能为空");
            }
            item.setEnabledFlag(DragonConstants.DRAGON_ENABLED_TRUE);
            message = "完成,需要您";
            dragonTask.setTaskState("3");//审核中
            dragonTask.setApprover(item.getApprover());
            taskLogs.setAssignee(item.getApprover());//指派给谁
            taskLogs.setType(DragonConstants.TASK_HANDLER_OPERATION_COMPLETE.toUpperCase());

        } else if (DragonConstants.TASK_HANDLER_OPERATION_ADOPT.equals(item.getOperation())) {
            //通过

            //通过不通过:审批中
            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState) || "0".equals(taskState)){
                return Response.error("该任务不能执行该操作:"+item.getOperation());
            }

            if (!dragonProjectLine.isManagerRoleForPro(dragonTask.getProjectId(), dragonUser.getId(), "") &&
                    dragonTask.getCreator() != dragonUser.getId() &&
                    dragonTask.getApprover() != dragonUser.getId()
//                    dragonTask.getAssignee() != dragonUser.getId()
                    ) {
                return Response.error("你没有通过这个任务的权限");
            }

            dragonTask.setTaskState("4");//已完成
            taskLogs.setType(DragonConstants.TASK_HANDLER_OPERATION_ADOPT.toUpperCase());
        } else if (DragonConstants.TASK_HANDLER_OPERATION_NOT_PASS.equals(item.getOperation())) {
            //不通过

            //通过不通过:审批中
            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState) || "0".equals(taskState)){

                return Response.error("该任务不能执行该操作:"+item.getOperation());
            }

            if (!dragonProjectLine.isManagerRoleForPro(dragonTask.getProjectId(), dragonUser.getId(), "") &&
                    dragonTask.getCreator() != dragonUser.getId() &&
                    dragonTask.getApprover() != dragonUser.getId()
                    ) {

                return Response.error("你没有不通过这个任务的权限");
            }
            item.setEnabledFlag(DragonConstants.DRAGON_ENABLED_TRUE);
            message = "不通过,需要您再";
            dragonTask.setTaskState("0");//未解决
            taskLogs.setType(DragonConstants.TASK_HANDLER_OPERATION_NOT_PASS.toUpperCase());
        }else {
            item.setEnabledFlag(DragonConstants.DRAGON_ENABLED_FALSE);
            return Response.error("没有该操作");
        }

        //更新部分task
        sw.buildQuery()
                .update("assignee",dragonTask.getAssignee())
                .update("approver",dragonTask.getApprover())
                .update("task_state",dragonTask.getTaskState())
                .table("mpaas_user")
                .eq("id", dragonTask.getId())
                .table("dragon_tasks_info")//指定更新表名
                .doUpdate();

        //设值,为发邮件准备,用creatorUserIcon指定是谁指定谁处理
        dragonTask = dragonTaskService.getSendEmailTask(dragonTask.getId(),
                message);
        dragonTask.setCreatorUserIcon(dragonUser.getUserName());
        dragonTask.setSendler(dragonUser.getUserName());
        item.setMyDragonTasks(dragonTask);

        //插入日志
        Object o = sw.buildQuery()
                .doInsert(taskLogs);
        logger.info(o+":插入日志");
        //若有资源上传则进行
        //文件上传通用,
        //多文件上传通用
        if (!ValidateUtils.checkIsNull(item.getLogResourceUrls()+"")){
            FndResources resources = new FndResources();
            Map map = DragonStringUtils.listToString(item.getLogResourceUrls());

            resources.setResourceName((String)map.get("usernames"));
            resources.setResourceUrl((String) map.get("urls"));
            resources.setTaskLogId(Integer.valueOf(o.toString()));
            resources.setEnabledFlag("TRUE");
            Object resourcesId = sw.buildQuery()
                    .doInsert(resources);
        }
        return Response.ok();
    }


    /**
     * 报表
     * @param map
     * @return
     */
    @RequestMapping(value = "/taskOverlord",method = RequestMethod.POST)
    public Response taskOverlord(@RequestBody Map map){
        String projectRowId = (String) map.get("projectRowId");
        if (ValidateUtils.checkIsNull(projectRowId)){
            return Response.error("项目rowId不能为空");
        }
        DragonProjects dragonProInfo = sw.buildQuery()
                .addRowIdClause("id","=",projectRowId)
                .doQueryFirst(DragonProjects.class);

        if (ValidateUtils.checkIsNull(dragonProInfo+"")){
            return Response.error("该项目部存在");
        }

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        int day = 30;
        String dateWhere = "  AND t.creation_date >= DATE_SUB(date_format(now(), '%Y-%m-%d'), INTERVAL "+day+" DAY) AND t.creation_date <= date_format(date_sub(now(), INTERVAL -1 DAY), '%Y-%m-%d')";
        String sql  = "SELECT COUNT(1) AS number ,( CASE t.task_state WHEN '0' THEN 'questionabilityNumber' WHEN '1' THEN 'delayNumber' WHEN '2' THEN 'closedNumber' WHEN '3' THEN 'auditingNumber' WHEN '4' THEN 'completeNumber' END) AS taskState FROM dragon_tasks_info t where t.project_id = "+dragonProInfo.getId()+dateWhere +" group by t.task_state";

        List<String> dates = DateUtil.getCurrDateByDays(day, simpleDateFormat);
        List<Map<String, Object>> tops = sw.buildQuery()
                .sql(sql)
                .doQuery();

        Map top = new HashMap<>();
        if (tops != null && tops.size() > 0){
            long count = 0L;
            top.put("questionabilityNumber",count);
            top.put("delayNumber",count);
            top.put("closedNumber",count);
            top.put("auditingNumber",count);
            top.put("completeNumber",count);
            for (Map myTop : tops){
                count+= ((long)myTop.get("number"));
                top.put(myTop.get("taskState"),myTop.get("number"));
            }
            top.put("taskNumber",count);
        }

        String baseNumberSql = "SELECT COUNT(1) AS taskNumber, date_format(t.creation_date, '%Y-%m-%d') AS taskDate FROM dragon_tasks_info t WHERE t.project_id = "+dragonProInfo.getId() +dateWhere;
        String orderSql = " GROUP BY date_format(t.creation_date, '%Y-%m-%d') ORDER BY date_format(t.creation_date, '%Y-%m-%d')";
        String taskSql = baseNumberSql+orderSql;

        List<Map<String, Object>> myTaskNumbers = sw.buildQuery()
                .sql(taskSql)
                .doQuery();

        List taskNumbers = getTaskNumber(dates,myTaskNumbers);

        String completeSql = baseNumberSql+" and  t.task_state = '4' "+orderSql;
        List<Map<String, Object>> myCompleteNumbers = sw.buildQuery()
                .sql(completeSql)
                .doQuery();
        List completeNumbers = getTaskNumber(dates,myCompleteNumbers);

        map = new HashMap();
        map.put("tops",top);
        map.put("dates",dates);
        map.put("completeNumbers",completeNumbers);
        map.put("taskNumbers",taskNumbers);

        return Response.ok().setData(map);
    }

    /**
     * 报表公告方法
     * @param abroadData 外层循环数据
     * @param sourceDate 内存循环(真实操作数据)
     * @return 得到的值
     */
    public List getTaskNumber(List<String> abroadData,List<Map<String,Object>> sourceDate){
        List data = new ArrayList(abroadData.size()+5);
        for (String str : abroadData){
            if (sourceDate != null && sourceDate.size() > 0){
                for (int i = 0;i<sourceDate.size();i++){
                    if (str.equals(sourceDate.get(i).get("taskDate"))){
                        data.add(sourceDate.get(i).get("taskNumber"));
                        sourceDate.remove(i);
                    }else {
                        data.add(0);
                    }
                    break;
                }
            }else {
                data.add(0);
            }
        }
        return data;
    }

    /**
     * 左侧边栏显示初始化
     * @param projectId 项目Id
     * @param userId 当前登录人Id
     * @return
     */
    public List<Map<String,Object>> showLeft(Integer projectId,Integer userId){
        String altMy = "select distinct alt from dragon_tasks_info";
        List<Map<String,Object>> altMys = sw.buildQuery()
                .sql(altMy)
                .eq("project_id",projectId)
                .doQuery();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map map1 : altMys){
            if (!ValidateUtils.checkIsNull(map1+"")){
                stringBuilder.append(map1.get("alt")+",");
            }
        }
        List list = null;

        list = (ArrayList)DragonStringUtils.strToCollection(stringBuilder.toString(),",",userId+"",list);

        String myToDoSql = "(SELECT COUNT(*) FROM dragon_tasks_info WHERE project_id = "+projectId+" AND ((task_state = 0 AND assignee = "+userId+") OR (task_state = 3 AND approver = "+userId+")))";
        //有个条件lookup_id=7写死了
        if (list == null){
            list = new ArrayList();
        }
        String sql = "SELECT v.lookup_code AS code, v.meaning AS name , CASE v.lookup_code WHEN 'activityProblem' THEN ( SELECT COUNT(*) FROM dragon_tasks_info WHERE task_state IN (0, 3) and project_id = "+projectId+" ) WHEN 'myToDo' THEN "+myToDoSql+" WHEN 'altMy' THEN "+list.size()+" ELSE 0 END AS size FROM fnd_lookup_values v WHERE v.enabled_flag = 'TRUE' AND v.lookup_id = 7";

        List<Map<String, Object>> maps = sw.buildQuery().sql(sql).doQuery();

        if (dragonProjectLine.isManagerRoleForPro(projectId,userId,"")){
            Map manager = new HashMap();
            manager.put("code","taskOverlord");
            manager.put("name","任务统计");
            manager.put("size",0);
            maps.add(manager);
        }
        return maps;
    }

}