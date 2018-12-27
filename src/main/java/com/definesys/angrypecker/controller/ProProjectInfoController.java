package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.*;
import com.definesys.angrypecker.service.DragonProjectLine;
import com.definesys.angrypecker.service.DragonUserService;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQueryFactory;
import com.definesys.mpaas.query.conf.MpaasQueryConfig;
import com.definesys.mpaas.query.db.PageQueryResult;
import com.definesys.mpaas.query.util.MpaasQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: 冰开水
 * @since: 2018-11-06
 * @history: 1.2018-11-06 created by 冰开水
 */
@RestController
@RequestMapping(value = "/api/projects")
public class ProProjectInfoController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @Autowired
    private DragonProjectLine dragonProjectLine;

    @Autowired
    private DragonUserService dragonUserService;

    @Autowired
    private MpaasQueryConfig config;

    /**
     * 开启项目时，完整性判断。
     * @param map
     * @return
     */
    @PostMapping(value = "/projectIntegrityCheck")
    public Response integrityCheck(@RequestBody Map<String,String> map){
        if(map==null)
            throw new MpaasBusinessException("参数不能为空");
        String rowId = map.get("rowId");
        if(ValidateUtils.checkIsNull(rowId))
            throw new MpaasBusinessException("项目id不能为空");
        String decryptRowIdStr = MpaasQueryUtil.decryptRowId(rowId, config.rowIdSecret);
        Integer decryptRowIdInt = Integer.valueOf(decryptRowIdStr);
        Map<String, Object> pid = sw.buildQuery()
                .sql("select COUNT(DISTINCT r.role_code) as size from dragon_project_role r,dragon_role_member m where r.project_id = #pid and r.role_code in('PRO_DIRECTOR','PRO_MANAGER','PRO_TECH_MANAGER','PRODUCT_MANAGER') and r.id = role_id and m.user_id is NOT NULL\n")
                .setVar("pid", decryptRowIdInt)
                .doQueryFirst();
        Object size = pid.get("size");
        if(size==null||Integer.valueOf(size.toString())<4)
            throw new MpaasBusinessException("notHaveBasicRoleForPro");
        return Response.ok();
    }

    /**
     * 查询项目详细信息
     * @param map 项目rowID
     * @return Response
     */
    @PostMapping(value = "/getProjectInfo")
    public Response getProjectInfo(@RequestBody Map map) {
        String rowId=(String) map.get("rowId");
        return Response.ok().table(sw.buildQuery()
                .sql("select * from dragon_projects")
                .addRowIdClause("id","=",rowId)
                .doQuery(DragonProjects.class));
    }

    /**
     * 获取我拥有的项目
     * @param map "page","pageSize"
     * @return
     */
    @RequestMapping(value = "/getMyProjects", method = RequestMethod.POST)
    public Response getMyProjects(@RequestBody Map map) {
        Integer page=(Integer) map.get("page");
        Integer pageSize=(Integer) map.get("pageSize");
        return sw.buildQuery()
                .sql("select t1.*,t2.sort from dragon_projects t1 right  JOIN dragon_user_project_info t2 on t1.id=t2.project_id")
                .addClause("project_owner_id","=",3)
                .addClause("status","=",1)
                .doPageQuery(page, pageSize, DragonProjects.class)
                .httpResponse();
    }

    /**
     * 查询关闭项目接口
     * @param map "page","pageSize"
     * @return
     */
    @RequestMapping(value = "/getMyCloseProjects", method = RequestMethod.POST)
    public Response getMyCloseProjects(@RequestBody Map map) {
        Integer page=(Integer) map.get("page");
        Integer pageSize=(Integer) map.get("pageSize");
        return sw.buildQuery()
                .sql("select t1.*,t2.sort from dragon_projects t1 right  JOIN dragon_user_project_info t2 on t1.id=t2.project_id")
                .addClause("project_owner_id","=",3)
                .addClause("status","=",0)
                .doPageQuery(page, pageSize, DragonProjects.class)
                .httpResponse();
    }


    /**
     * 创建项目接口
     * @param
     * @return
     */
    @RequestMapping(value = "/createProject",method = RequestMethod.POST )
    public Response createProject(@RequestBody DragonProjects dragonProjects) {
        DragonProjects proProjectInfo = new DragonProjects();
        DragonUser user = dragonUserService.getDragonUser();
        Integer userId = user.getId();
        String userName = user.getUserName();

        if(dragonProjects.getId()==null) {
            dragonProjects.setProjectOwnerId(userId);
            dragonProjects.setStatus("1");
            Object key = sw.buildQuery()
                    .bind(dragonProjects)
                    .doInsert();

            if (key != null) {
                Integer projectId = Integer.parseInt(key.toString());
                //插入四个默认项目角色
                DragonProjectRole item = new DragonProjectRole();
                item.setProjectId(projectId);
                item.setRoleCode("PRO_DIRECTOR");
                sw.buildQuery()
                        .bind(item)
                        .doInsert();
                item.setRoleCode("PRO_MANAGER");
                sw.buildQuery()
                        .bind(item)
                        .doInsert();
                item.setRoleCode("PRO_TECH_MANAGER");
                sw.buildQuery()
                        .bind(item)
                        .doInsert();
                item.setRoleCode("PRODUCT_MANAGER");
                sw.buildQuery()
                        .bind(item)
                        .doInsert();
                DragonProjectModuleInfo dragonProjectModuleInfo = new DragonProjectModuleInfo();
                dragonProjectModuleInfo.setProjectId(projectId);
                dragonProjectModuleInfo.setModuleName("默认模块");
                sw.buildQuery()
                        .bind(dragonProjectModuleInfo)
                        .doInsert();
                //插入序号sort
                Object sortId = dragonProjectLine.insertProSort(userId, projectId);
                //获取新增后的项目
                proProjectInfo = sw.buildQuery()
                        .addClause("id", "=", projectId)
                        .doQueryFirst(DragonProjects.class);
                proProjectInfo.setOwnerName(userName);
                proProjectInfo.setSend(TRUE);
                proProjectInfo.setSortId(Integer.parseInt(sortId.toString()));
            }
        }
        else {
            DragonProjects projects = sw.buildQuery()
                    .addClause("id","=",dragonProjects.getId())
                    .doQueryFirst(DragonProjects.class);
            if(projects!=null) {
                dragonProjects.setProjectOwnerId(projects.getProjectOwnerId());
                dragonProjects.setStatus(projects.getStatus());
                sw.buildQuery()
                        .addClause("id", "=", dragonProjects.getId())
                        .doUpdate(dragonProjects);
            }
        }
        return Response.ok().data(proProjectInfo);
    }

    /**
     * 删除项目接口
     * @param map UUID
     * @return response
     */
    @RequestMapping(value = "/deleteProProjectInfo", method = RequestMethod.POST)
    public Response deleteProProjectInfo(@RequestBody Map map) {
        String rowId=(String) map.get("rowId");
//        //获取关系表id
//        List<DragonProjects> dragonProjects=sw.buildQuery()
//                .select("id")
//                .doQuery(DragonProjects.class);
        //删除项目
        sw.buildQuery()
                .bind(DragonProjects.class)
                .addRowIdClause("id", "=", rowId)
                .doDelete();
//        //删除项目与拥有者联系
//        sw.buildQuery()
//                .addClause("project_id","=",dragonProjects.get(0).getId());
        return Response.ok();
    }


    /**
     * 操作项目接口 关闭项目/变更项目拥有人/恢复项目
     * v.wang
     * @param map UUID
     * @return response
     */
    @RequestMapping(value = "/operationProject", method = RequestMethod.POST)
    @Transactional
    public Response closeProject(@RequestBody Map map) {
        //项目Id
        String projectId=(String) map.get("projectId");
        String operation=(String) map.get("operation");
        //项目拥有者
        Integer projectOwnerId=(Integer) map.get("projectOwnerId");

        if (ValidateUtils.checkIsNull(projectId+""))
            return Response.error("项目不能为空");
        DragonProjects dragonProject = sw.buildQuery()
                .addRowIdClause("id", "=", projectId)
                .doQueryFirst(DragonProjects.class);
        if (ValidateUtils.checkIsNull(dragonProject+""))
            return Response.error("没有该项目");
        DragonUser dragonUser = dragonUserService.getDragonUser();
        if (!dragonProjectLine.isManagerRoleForPro(dragonProject.getId(),dragonUser.getId(),"")){
            return Response.error("你没有权限操作");
        }

        if ("closeProject".equals(operation)){

            if ("0".equals(dragonProject.getStatus())){
                return Response.error("该项目已关闭");
            }

            //关闭项目
            dragonProject.setStatus("0");
            sw.buildQuery()
                    .eq("id", dragonProject.getId())
                    .doUpdate(dragonProject);
            sw.buildQuery()
                    .bind(DragonTasks.class)
                    .eq("project_id", dragonProject.getId())
                    .update("enabled_flag","FALSE")
                    .doUpdate();
            return Response.ok().setMessage("关闭项目成功");
        }else if ("changeProjectOwner".equals(operation)){
            //变更项目拥有者
            if (ValidateUtils.checkIsNull(projectOwnerId+"")){
                return Response.error("需要变更项目拥有者不能为空");
            }
            if (!"1".equals(dragonProject.getStatus())){
                return Response.error("该项目不能变更拥有者");
            }
            //关闭项目之后能否变更拥有者

            sw.buildQuery()
                    .bind(DragonProjects.class)
                    .eq("id", dragonProject.getId())
                    .update("project_owner_id",projectOwnerId)
                    .doUpdate();

            if(!dragonProjectLine.isHaveRole(dragonProject.getId(),dragonProject.getProjectOwnerId())){
                sw.buildQuery()
                        .bind(DragonUserProjectInfo.class)
                        .addClause("user_id","=",dragonProject.getProjectOwnerId())
                        .addClause("project_id","=",dragonProject.getId())
                        .doDelete();
            }
            if(!dragonProjectLine.isHaveRole(dragonProject.getId(),projectOwnerId)){
                dragonProjectLine.insertProSort(projectOwnerId,dragonProject.getId());
            }

            return Response.ok().setMessage("变更项目拥有者成功");
        }else if ("recovery".equals(operation)){
            if ("1".equals(dragonProject.getStatus())){
                return Response.error("该项目已是恢复状态");
            }

            //关闭项目
            dragonProject.setStatus("1");
            sw.buildQuery()
                    .eq("id", dragonProject.getId())
                    .doUpdate(dragonProject);
            sw.buildQuery()
                    .bind(DragonTasks.class)
                    .eq("project_id", dragonProject.getId())
                    .update("enabled_flag","TRUE")
                    .doUpdate();
            return Response.ok().setMessage("恢复项目成功");
        }
        return Response.error("请输入具体操作");
    }


    /**
     * 项目基础信息更新接口
     * @param item "project_name","project_desc","project_logo"
     * @return Response.ok()
     */
    @RequestMapping(value = "/updateBase", method = RequestMethod.POST)
    public Response updateBase(@RequestBody DragonProjects item) {
        //部分字段更新
        String[] updateFields = new String[]{"project_name","project_desc","project_logo"};
        sw.buildQuery()
                .bind(item)
                .addRowIdClause("id", "=", item.getRowId())
                .update("project_name",item.getProjectName())
                .update("project_desc",item.getProjectDesc())
                .update("project_logo",item.getProjectLogo())
                .update(updateFields)
                .doUpdate();
        return Response.ok();
    }


    /**
     * 获取项目列表
     * Chico Lee
     * @param map "page","pageSize"
     * @return
     */
    @RequestMapping(value = "/getProjectForData", method = RequestMethod.POST)
    public Response getProjectForData(@RequestBody Map map) {
        Integer page=(Integer) map.get("page");
        Integer pageSize=(Integer) map.get("pageSize");
        String queryType = (String) map.get("queryType");
        DragonUser user = dragonUserService.getDragonUser();
        Integer userId = user.getId();

        List<DragonProInfo> projectInfos = new ArrayList<>();
        if("allPro".equals(queryType)) {
            PageQueryResult<DragonProInfo> pageQuery = sw.buildQuery()
                    .sql("SELECT DISTINCT dp.id as id,dp.status,dp.project_desc ,dp.creation_date,dp.project_name,dp.project_logo,dp.project_owner_id, \n" +
                            "(SELECT fu.user_name FROM fnd_users fu where fu.id = dp.project_owner_id) ownerName," +
                            "(select count(*) from dragon_tasks_info ti where ti.project_id = dp.id and ((ti.task_state = '0' and ti.assignee = "+userId+" ) or (ti.task_state = '3' and ti.approver = "+userId+" ) ) )gtasksNum\n" +
                            " FROM `dragon_projects` dp\n" +
                            "LEFT JOIN dragon_project_role dpr ON dpr.project_id = dp.id\n" +
                            " LEFT JOIN dragon_role_member drm ON drm.role_id = dpr.id")
                    .or()
                    .eq("userId", userId)
                    .eq("projectOwnerId",userId)
                    .conjuctionAnd()
                    .and()
                    .eq("status","1")
                    .doPageQuery(page, pageSize, DragonProInfo.class);
            projectInfos = pageQuery.getResult();
            projectInfos = dragonProjectLine.getProjectLineMsg(projectInfos, userId);
        }
        else if("myPro".equals(queryType)){
            PageQueryResult<DragonProInfo> pageQuery = sw.buildQuery()
                    .sql("SELECT DISTINCT dp.id as id,dp.status,dp.project_desc ,dp.creation_date,dp.project_name,dp.project_logo,dp.project_owner_id, \n" +
                            "(SELECT fu.user_name FROM fnd_users fu where fu.id = dp.project_owner_id) ownerName," +
                            "(select count(*) from dragon_tasks_info ti where ti.project_id = dp.id and ((ti.task_state = '0' and ti.assignee = "+userId+" ) or (ti.task_state = '3' and ti.approver = "+userId+" )) )gtasksNum\n" +
                            " FROM `dragon_projects` dp\n" +
                            "LEFT JOIN dragon_project_role dpr ON dpr.project_id = dp.id\n" +
                            " LEFT JOIN dragon_role_member drm ON drm.role_id = dpr.id")
                    .addClause("project_owner_id","=",userId)
                    .addClause("status","=","1")
                    .doPageQuery(page, pageSize, DragonProInfo.class);
            projectInfos = pageQuery.getResult();
            projectInfos = dragonProjectLine.getProjectLineMsg(projectInfos, userId);
        }
        else if("closePro".equals(queryType)) {
            projectInfos = sw.buildQuery()
                    .sql("SELECT DISTINCT dp.id as id,dp.status,dp.project_desc ,dp.creation_date,dp.project_name,dp.project_logo,dp.project_owner_id, \n" +
                            "(SELECT fu.user_name FROM fnd_users fu where fu.id = dp.project_owner_id) ownerName," +
                            "(select count(*) from dragon_tasks_info ti where ti.project_id = dp.id and ((ti.task_state = '0' and ti.assignee = "+userId+" ) or (ti.task_state = '3' and ti.approver = "+userId+" )) )gtasksNum\n" +
                            " FROM `dragon_projects` dp\n" +
                            "LEFT JOIN dragon_project_role dpr ON dpr.project_id = dp.id\n" +
                            " LEFT JOIN dragon_role_member drm ON drm.role_id = dpr.id")
                    .or()
                    .eq("userId", userId)
                    .eq("projectOwnerId",userId)
                    .conjuctionAnd()
                    .and()
                    .eq("status","0")
                    .doQuery(DragonProInfo.class);

            List<DragonProInfo> dragonProjectInfos = new ArrayList<>();
            if (projectInfos.size() > 0) {
                for (DragonProInfo projectInfo : projectInfos) {
                    if (userId.equals(projectInfo.getProjectOwnerId())) {
                        dragonProjectInfos.add(projectInfo);
                    } else if (dragonProjectLine.isManagerRoleForPro(projectInfo.getId(), userId, "T")) {
                        dragonProjectInfos.add(projectInfo);
                    }
                }
                projectInfos = dragonProjectLine.getProjectLineMsg(dragonProjectInfos, userId);

                int end = page * pageSize;
                int start = (page - 1) * pageSize;
                int linkSize = projectInfos.size();
                if (start < linkSize) {
                    if (end < linkSize) {
                        projectInfos = projectInfos.subList(start, end);
                    } else {
                        projectInfos = projectInfos.subList(start, linkSize);
                    }
                }
            }
        }
        return Response.ok().setData(projectInfos);
    }




    /**
     * 获取项目列表数量
     * Chico Lee
     * @param
     * @return
     */
    @RequestMapping(value = "/getProjectListSize", method = RequestMethod.POST)
    public Response getProjectListSize() {
        DragonUser user = dragonUserService.getDragonUser();
        Integer userId = user.getId();
        List<Map> list = new ArrayList<>();

        List<DragonProInfo> allList = sw.buildQuery()
                .sql("SELECT DISTINCT dp.id as id,dp.status,dp.project_desc ,dp.creation_date,dp.project_name,dp.project_logo,dp.project_owner_id, \n" +
                        "(SELECT fu.user_name FROM fnd_users fu where fu.id = dp.project_owner_id) ownerName\n" +
                        " FROM `dragon_projects` dp\n" +
                        "LEFT JOIN dragon_project_role dpr ON dpr.project_id = dp.id\n" +
                        " LEFT JOIN dragon_role_member drm ON drm.role_id = dpr.id")
                .or()
                .eq("userId", userId)
                .eq("projectOwnerId",userId)
                .conjuctionAnd()
                .and()
                .eq("status","1")
                .doQuery(  DragonProInfo.class);
        Map<String,Object> allMap = new HashMap<>();
        allMap.put("name","所有项目");
        allMap.put("size",allList.size());
        list.add(allMap);

        List<DragonProInfo> myList = sw.buildQuery()
                .sql("SELECT DISTINCT dp.id as id,dp.status,dp.project_desc ,dp.creation_date,dp.project_name,dp.project_logo,dp.project_owner_id, \n" +
                        "(SELECT fu.user_name FROM fnd_users fu where fu.id = dp.project_owner_id) ownerName\n" +
                        " FROM `dragon_projects` dp\n" +
                        "LEFT JOIN dragon_project_role dpr ON dpr.project_id = dp.id\n" +
                        " LEFT JOIN dragon_role_member drm ON drm.role_id = dpr.id")
                    .addClause("projectOwnerId","=",userId)
                    .addClause("status","=","1")
                    .doQuery( DragonProInfo.class);
        Map<String,Object> myMap = new HashMap<>();
        myMap.put("name","我的项目");
        myMap.put("size",myList.size());
        list.add(myMap);


        List<DragonProInfo> closeList = sw.buildQuery()
                .sql("SELECT DISTINCT dp.id as id,dp.status,dp.project_desc ,dp.creation_date,dp.project_name,dp.project_logo,dp.project_owner_id, \n" +
                        "(SELECT fu.user_name FROM fnd_users fu where fu.id = dp.project_owner_id) ownerName\n" +
                        " FROM `dragon_projects` dp\n" +
                        "LEFT JOIN dragon_project_role dpr ON dpr.project_id = dp.id\n" +
                        " LEFT JOIN dragon_role_member drm ON drm.role_id = dpr.id")
                .or()
                .eq("userId", userId)
                .eq("projectOwnerId",userId)
                .conjuctionAnd()
                .and()
                .eq("status","0")
                .doQuery(DragonProInfo.class);

        List<DragonProInfo> dragonProjectInfos = new ArrayList<>();
        if (closeList.size() > 0) {
            for (DragonProInfo projectInfo : closeList) {
                if (userId.equals(projectInfo.getProjectOwnerId())) {
                    dragonProjectInfos.add(projectInfo);
                } else if (dragonProjectLine.isManagerRoleForPro(projectInfo.getId(), userId, "T")) {
                    dragonProjectInfos.add(projectInfo);
                }
            }
        }
        Map<String,Object> closeMap = new HashMap<>();
        closeMap.put("name","关闭的项目");
        closeMap.put("size",dragonProjectInfos.size());
        list.add(closeMap);
        return Response.ok().setData(list);
    }



    /**
     * 导出excel
     * @param response
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) {
        sw.buildQuery()
                .fileName("controller")
                .doExport(response, DragonProjects.class);
    }
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response query() {
        List<DragonProjects> data = sw.buildQuery()
                .fileName("controller")
                .doQuery(DragonProjects.class);
        return Response.ok().setData(data);
    }

}