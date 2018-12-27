package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.*;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: 阳
 * @since: 2018-11-23
 * @history: 1.2018-11-23 created by 阳
 */
@RestController
@RequestMapping(value = "/api/role")
public class DragonProjectRoleController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response queryDragonProjectRole() {
        List<DragonProjectRole> table = sw.buildQuery()
                .doQuery(DragonProjectRole.class);
        return Response.ok().table(table);
    }

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    public Response pageQueryDragonProjectRole(@RequestParam(value = "page") Integer page,
                                               @RequestParam(value = "pageSize") Integer pageSize) {
        return sw.buildQuery()
                .doPageQuery(page, pageSize, DragonProjectRole.class)
                .httpResponse();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response addDragonProjectRole(@RequestBody DragonProjectRole item) {
        Object key = sw.buildQuery()
                .bind(item)
                .doInsert();
        return Response.ok().data(key);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Response deleteDragonProjectRole(@RequestParam(value = "rowId") String rowId) {
        sw.buildQuery()
                .bind(DragonProjectRole.class)
                .addRowIdClause("id", "=", rowId)
                .doDelete();
        return Response.ok();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response updateDragonProjectRole(@RequestBody DragonProjectRole item) {
        sw.buildQuery()
                .addRowIdClause("id", "=", item.getRowId())
                .update("is_disaplayed",item.getIsDisaplayed())
                .doUpdate(item);
        return Response.ok();
    }


    /**
     * 删除项目角色
     * Chico Lee
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteProjectRole", method = RequestMethod.POST)
    public Response deleteProjectRole(@RequestBody Map<String,Object> map) {
        Integer roleId = (Integer) map.get("roleId");
        Integer projectId = (Integer)map.get("projectId");
        Map userId = sw.buildQuery()
                .sql("select user_id from dragon_role_member")
                .addClause("role_id","=",roleId)
                .doQueryFirst();

        sw.buildQuery()
                .bind(DragonRoleMember.class)
                .addClause("role_id","=",roleId)
                .doDelete();

        sw.buildQuery()
                .bind(DragonProjectRole.class)
                .addClause("id", "=", roleId)
                .doDelete();

        if(userId!=null) {
            sw.buildQuery()
                    .bind(DragonUserProjectInfo.class)
                    .addClause("user_id", "=", (Integer) userId.get("user_id"))
                    .addClause("project_id", "=", projectId)
                    .doDelete();
        }
        return Response.ok();
    }

    /**
     * 添加项目角色
     * Chico Lee
     * @param
     * @return
     */
    @RequestMapping(value = "/addProjectRole", method = RequestMethod.POST)
    public Response addProjectRole(@RequestBody DragonProjectRole item) {
        Object key = new Object();
        if(item.getId()==null) {
            key = sw.buildQuery()
                    .bind(item)
                    .doInsert();
        }else {
            sw.buildQuery()
                    .addClause("id","=",item.getId())
                    .doUpdate(item);
            key = item.getId();
        }
        return Response.ok().data(key);
    }

    /**
     * 获取所有角色成员
     * Chico Lee
     * @return
     */
    @RequestMapping(value = "/getProjectRoleQuery", method = RequestMethod.POST)
    public Response getProjectRoleQuery(@RequestBody Map<String, Integer> map) {
            Integer projectId = (Integer) map.get("projectId") ;
        List<ProjectMember> table = sw.buildViewQuery("projectmember_v")
                .addClause("projectId","=",projectId)
                .doQuery(ProjectMember.class);
        return Response.ok().table(table);
    }

    /**
     * 获取所有项目成员
     * Chico Lee
     * @return
     */
    @RequestMapping(value = "/getAllMemberForPro", method = RequestMethod.POST)
    public Response getAllMemberForPro(@RequestBody Map<String, Integer> map) {
        Integer projectId = (Integer) map.get("projectId") ;
        if(projectId==null||projectId<=0)
            return Response.error("不存在该项目");
         List<DragonProUser> dragonProUsers =  sw.buildViewQuery("dragonprouser_v")
                 .addClause("projectId","=",projectId)
                 .doQuery(DragonProUser.class);
        return Response.ok().data(dragonProUsers);
    }

    /**
     * 导出excel
     *
     * @param response
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) {
        sw.buildQuery("dragonprojectrole_v")
                .fileName("controller.xlsx")
                .doExport(response, DragonProjectRole.class);
    }

}