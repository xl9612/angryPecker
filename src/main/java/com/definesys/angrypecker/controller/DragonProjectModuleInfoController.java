package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonProjectModuleInfo;
import com.definesys.angrypecker.service.DragonModuleService;
import com.definesys.angrypecker.util.common.CURDUtils;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQuery;
import com.definesys.mpaas.query.MpaasQueryFactory;
import com.definesys.mpaas.query.conf.MpaasQueryConfig;
import com.definesys.mpaas.query.util.MpaasQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: xulei
 * @since: 2018-11-22
 * @history: 1.2018-11-22 created by xulei
 */
@RestController
@RequestMapping(value = "/api/module")
public class DragonProjectModuleInfoController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @Autowired
    private DragonModuleService dragonModuleService;

    @Autowired
    private MpaasQueryConfig config;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response queryDragonProjectModuleInfo() {
        List<DragonProjectModuleInfo> table = sw.buildQuery()
                .doQuery(DragonProjectModuleInfo.class);
        return Response.ok().table(table);
    }

    /**
     * 根据项目id获取，该项目的所有模块
     *
     * @param map 所属项目id
     * @return
     */
    @PostMapping(value = "/queryByProId")
    public Response queryByRowid(@RequestBody Map<String,String> map) {
        //获取前端传递过来的rowId
        String rowId = map.get("rowId");
        //新建一个list，用来存储所有返回给前端的模块对象
        List<Map<String,Object>> resultMap = new ArrayList<>();
        //根据proid，查询所有所属模块的信息。
        String id = MpaasQueryUtil.decryptRowId(rowId, config.rowIdSecret);
        Integer proId = Integer.valueOf(id);
        List<DragonProjectModuleInfo> list = sw.buildQuery()
                .select("id,module_name,assign_id,approver_id")
                .eq("project_id",proId)
                .doQuery(DragonProjectModuleInfo.class);
        //判断该项目是否存在模块
        if(list.size()>0){
            //循环遍历该项目的所有模块
            for (DragonProjectModuleInfo moduleInfo:list
                 ) {
                //创建一个map用来存储一个模块对象的所有信息
                Map<String,Object> objMap = new HashMap<>();
                //设置模块id
                objMap.put("id",moduleInfo.getId());
                objMap.put("rowId",moduleInfo.getRowId());
                //设置模块所属项目id
                objMap.put("projectId",proId);
                //设置模块名
                objMap.put("moduleName",moduleInfo.getModuleName());
                //预先设置该模块处理人id
                objMap.put("assignId",null);
                //预先设置该模块处理人姓名
                objMap.put("assignName",null);
                objMap.put("assignIcon",null);
                //预先设置该模块审批人id为空
                objMap.put("approverId",null);
                //预先设置该模块审批人姓名为空
                objMap.put("approverName",null);
                objMap.put("approverIcon",null);
                //获取该模块处理人id
                Integer assignId = moduleInfo.getAssignId();
                //获取该模块审批人id
                Integer approverId = moduleInfo.getApproverId();
                //如果该模块处理人为空，则不做查询处理人姓名的操作
                if(assignId!=null){
                    objMap.put("assignId",assignId);
                    Map<String, Object> stringObjectMap = sw.buildQuery()
                            .sql("select user_name assignName,user_icon assignIcon from fnd_users")
                            .eq("id",assignId)
                            .doQueryFirst();
                    if(stringObjectMap!=null)
                        objMap.putAll(stringObjectMap);

                }
                //如果该模块审批人为空，则不做查询审批人姓名的操作
                if(approverId!=null){
                    objMap.put("approverId",approverId);
                    Map<String, Object> stringObjectMap = sw.buildQuery()
                            .sql("select user_name approverName,user_icon approverIcon from fnd_users")
                            .eq("id", approverId)
                            .doQueryFirst();
                    if(stringObjectMap!=null)
                        objMap.putAll(stringObjectMap);
                }
                resultMap.add(objMap);
            }
        }
        return Response.ok().table(resultMap);
    }

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    public Response pageQueryDragonProjectModuleInfo(@RequestParam(value = "page") Integer page,
                                                     @RequestParam(value = "pageSize") Integer pageSize) {
        return sw.buildQuery()
                .doPageQuery(page, pageSize, DragonProjectModuleInfo.class)
                .httpResponse();
    }

    /**
     * 添加模块
     *
     * @param item 待添加的模块信息
     * @return 返回已添加的模块
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response addDragonProjectModuleInfo(@RequestBody DragonProjectModuleInfo item) {
        MpaasQuery query = sw.buildQuery();
        //判断当前项目下该模块名是否重复
        CURDUtils.isModuleNameExist(query, item);
        if(item.getProjectId()==null||item.getProjectId()<=0)
            throw new MpaasBusinessException("该模块所在项目不存在");
        Object key = query
                .bind(item)
                .doInsert();
        Map<String, Object> map = query.sql("select id,project_id,module_name,assign_id,approver_id from dragon_project_module_info")
                .eq("id", key)
                .doQueryFirst();
        return Response.ok().data(map).setMessage("操作成功");
    }

    /**
     * 删除模块及模块中的任务
     *
     * @return
     */

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Response deleteDragonProjectModuleInfo(@RequestBody Map<String, String> map) {
        MpaasQuery query = sw.buildQuery();
        dragonModuleService.deleteModule(sw, map.get("rowId"));
        return Response.ok().setMessage("操作成功");
    }

    /**
     * 更新模块信息
     *
     * @param item
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response updateDragonProjectModuleInfo(@RequestBody DragonProjectModuleInfo item) {
        MpaasQuery query = sw.buildQuery();
        //判断模块名是否重复
        CURDUtils.isModuleNameExist(query, item);
        CURDUtils.update(query, item, item.getRowId(), new String[]{"module_name", "assign_id", "approver_id"});
        return Response.ok().setMessage("操作成功");
    }

    /**
     * 导出excel
     *
     * @param response
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) {
        sw.buildQuery("dragonprojectmoduleinfo_v")
                .fileName("xlt.xlsx")
                .doExport(response, DragonProjectModuleInfo.class);
    }


}