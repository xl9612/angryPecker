package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonRoleMember;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.pojo.DragonUserProjectInfo;
import com.definesys.angrypecker.service.DragonProjectLine;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
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
@RequestMapping(value = "/api/projectMember")
public class DragonRoleMemberController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @Autowired
    private DragonProjectLine dragonProjectLine;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response queryDragonRoleMember() {
        List<DragonRoleMember> table = sw.buildQuery()
                .doQuery(DragonRoleMember.class);
        return Response.ok().table(table);
    }

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    public Response pageQueryDragonRoleMember(@RequestParam(value = "page") Integer page,
                                              @RequestParam(value = "pageSize") Integer pageSize) {
        return sw.buildQuery()
                .doPageQuery(page, pageSize, DragonRoleMember.class)
                .httpResponse();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response addDragonRoleMember(@RequestBody DragonRoleMember item) {
        Object key = sw.buildQuery()
                .bind(item)
                .doInsert();
        return Response.ok().data(key);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Response deleteDragonRoleMember(@RequestParam(value = "rowId") String rowId) {
        sw.buildQuery()
                .bind(DragonRoleMember.class)
                .addRowIdClause("id", "=", rowId)
                .doDelete();
        return Response.ok();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response updateDragonRoleMember(@RequestBody DragonRoleMember item) {
        sw.buildQuery()
                .addRowIdClause("id", "=", item.getRowId())
                .doUpdate(item);
        return Response.ok();
    }

    /**
     * 添加项目成员
     * Chico Lee
     * @param item
     * @return
     */
    @RequestMapping(value = "/addRoleMember", method = RequestMethod.POST)
    public Response addRoleMember(@RequestBody DragonRoleMember item) {
        Object key = new Object();

        DragonRoleMember roleMember = sw.buildQuery()
                .addClause("role_id","=",item.getRoleId())
                .doQueryFirst(DragonRoleMember.class);


        if(roleMember==null) {
            DragonUser dragonUser = sw.buildQuery()
                    .sql("select * from fnd_users")
                    .addClause("login_email", "=", item.getLoginEmail())
                    .doQueryFirst(DragonUser.class);
            if (dragonUser == null) {
                throw new MpaasBusinessException("此邮箱还未注册，请重新输入邮箱!");
            }
            item.setUserId(dragonUser.getId());
            key = sw.buildQuery()
                    .bind(item)
                    .doInsert();

            dragonProjectLine.insertProSort(dragonUser.getId(), item.getProjectId());
        }
        else {
            DragonUser dragonUser = sw.buildQuery()
                    .sql("select * from fnd_users")
                    .addClause("login_email", "=", item.getLoginEmail())
                    .doQueryFirst(DragonUser.class);
            if (dragonUser == null) {
                throw new MpaasBusinessException("此邮箱还未注册，请重新输入邮箱!");
            }
            item.setUserId(dragonUser.getId());
            sw.buildQuery()
                    .addClause("role_id", "=", item.getRoleId())
                    .doUpdate(item);

            sw.buildQuery()
                    .bind(DragonUserProjectInfo.class)
                    .addClause("user_id", "=", roleMember.getUserId())
                    .addClause("project_id", "=", item.getProjectId())
                    .doDelete();

            dragonProjectLine.insertProSort(item.getUserId(), item.getProjectId());
            key = roleMember.getId();
        }
        return Response.ok().data(key);
    }

    /**
     * 删除项目成员
     * Chico Lee
     * @param map
     * @return
     */
    @RequestMapping(value = "/deleteRoleMember", method = RequestMethod.POST)
        public Response deleteDragonRoleMember(@RequestBody Map<String ,Integer> map) {
        Integer memberId = map.get("memberId");
        Integer projectId = map.get("projectId");
        Map userId = sw.buildQuery()
                .sql("select user_id from dragon_role_member")
                .addClause("id","=",memberId)
                .doQueryFirst();

        if(userId!=null) {
            sw.buildQuery()
                    .bind(DragonRoleMember.class)
                    .addClause("id", "=", memberId)
                    .doDelete();
            sw.buildQuery()
                    .bind(DragonUserProjectInfo.class)
                    .addClause("user_id", "=", (Integer) userId.get("user_id"))
                    .addClause("project_id", "=", projectId)
                    .doDelete();
        }else {
            return Response.error("未找到该成员");
        }
        return Response.ok();
    }

    /**
     * 导出excel
     *
     * @param response
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) {
        sw.buildQuery("dragonrolemember_v")
                .fileName("controller.xlsx")
                .doExport(response, DragonRoleMember.class);
    }

}