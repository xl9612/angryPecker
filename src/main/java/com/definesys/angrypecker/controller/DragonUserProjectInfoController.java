package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonProInfo;
import com.definesys.angrypecker.pojo.DragonProjects;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.pojo.DragonUserProjectInfo;
import com.definesys.angrypecker.service.DragonUserService;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQueryFactory;
import com.definesys.mpaas.query.conf.MpaasQueryConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: 冰开水
 * @since: 2018-11-12
 * @history: 1.2018-11-12 created by 冰开水
 */
@RestController
@RequestMapping(value = "/api/userAndProject/")
public class DragonUserProjectInfoController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SWordLogger logger;

    @Autowired
    private DragonUserService dragonUserService;

    @Autowired
    private MpaasQueryConfig config;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response queryDragonUserProjectInfo() {
        List<DragonUserProjectInfo> table = sw.buildQuery()
                .doQuery(DragonUserProjectInfo.class);
        return Response.ok().table(table);
    }

    /**
     * 查询项目下所有人员接口
     * @param map "projectId"
     * @return table
     */
    @RequestMapping(value = "/queryAllUserByProject", method = RequestMethod.POST)
    public Response queryAllUserByProject(@RequestBody Map map) {
        String sql="select t1.user_name,t1.id,t1.user_icon from fnd_users t1 RIGHT  JOIN dragon_user_project_info t2 on t1.id=t2.user_id where project_id = %d ";
        String result=String.format(sql,(Integer)map.get("projectId"));
        List<DragonUser> table = sw.buildQuery()
                .sql(result)
                .doQuery(DragonUser.class);
        return Response.ok().table(table);

    }

    /**
     * 查询全部项目接口
     * @param map "page","pageSize"
     * @return table
     */
    @RequestMapping(value = "/getAllProjects", method = RequestMethod.POST)
    public Response getAllProjects(@RequestBody Map map) {
        Integer userId=3;
        Integer page=(Integer) map.get("page");
        Integer pageSize=(Integer) map.get("pageSize");
        String sql="select t1.*,t2.sort from dragon_projects t1 right  JOIN dragon_user_project_info t2 on t1.id=t2.project_id  where user_id = %d and t1.status=1";
        String result=String.format(sql,userId);
        return sw.buildQuery()
                .sql(result)
                .doPageQuery(page, pageSize, DragonProjects.class)
                .httpResponse();


    }

    /**
     * 项目排序
     * @param list
     * @return
     */
    @RequestMapping(value = "/changeSort", method = RequestMethod.POST)
    public Response changeSort(@RequestBody List<DragonProInfo> list) {
        DragonUser user = dragonUserService.getDragonUser();
        Integer userId = user.getId();

        Integer arr=list.size();
        for(int i=0;i<arr-1;i++) {

            for (int j = 0; j < arr -1 - i; j++) {
                //交换位置
                if (list.get(j).getSort()>list.get(j+1).getSort()){
                    Integer temp=list.get(j).getSort();
                    list.get(j).setSort(list.get(j+1).getSort());
                    list.get(j+1).setSort(temp);
                }

            }
        }
        for (DragonProInfo dragonProjects:list) {
            Integer sort=dragonProjects.getSort();
            Integer id=dragonProjects.getId();
            sw.buildQuery()
                    .update("sort",sort)
                    .addClause("project_id","=",id)
                    .addClause("user_id","=",userId)
                    .table("dragon_user_project_info")
                    .doUpdate();
        }

        return Response.ok().table(list);
    }


    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    public Response pageQueryDragonUserProjectInfo(@RequestParam(value = "page")Integer page,
                                            @RequestParam(value = "pageSize")Integer pageSize) {
        return sw.buildQuery()
                .sql("select * from dragon_user_project_info")
                .doPageQuery(page, pageSize, DragonUserProjectInfo.class)
                .httpResponse();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response addDragonUserProjectInfo(@RequestBody DragonUserProjectInfo item) {
        Object key = sw.buildQuery()
                .bind(item)
                .doInsert();
        return Response.ok().data(key);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Response deleteDragonUserProjectInfo(@RequestParam(value = "rowId") String rowId) {
        sw.buildQuery()
                .bind(DragonUserProjectInfo.class)
                .addRowIdClause("id", "=", rowId)
                .doDelete();
        return Response.ok();
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
                .doExport(response, DragonUserProjectInfo.class);
    }

    /**
     * 项目是否开启邮件通知
     * @return
     */
    @PostMapping(value = "/msgNotify")
    public Response setMsgNotification(@RequestBody DragonUserProjectInfo dragonUserProjectInfo){
        String isNotification = dragonUserProjectInfo.getIsNotification();
        if(ValidateUtils.checkIsNull(isNotification))
            throw new MpaasBusinessException("请确认是否开启邮件通知");
        if(!"1".equals(isNotification)&&!"0".equals(isNotification))
            throw new MpaasBusinessException("参数不符合");
        DragonUser dragonUser = dragonUserService.getDragonUser();
        sw.buildQuery()
                .bind(dragonUserProjectInfo)
                .update(new String[]{"is_notification"})
                .eq("user_id",dragonUser.getId())
                .eq("project_id",dragonUserProjectInfo.getProjectId())
                .doUpdate(dragonUserProjectInfo);
        return Response.ok();
    }

}