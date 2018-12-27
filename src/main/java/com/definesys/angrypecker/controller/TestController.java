package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonTasksExtend;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {
    @Autowired
    private MpaasQueryFactory sw;

    @RequestMapping("/static/hello")
    public String hello(@RequestBody Map map, DragonTasksExtend dragonTasksExtend)throws Exception{
        String name = (String)map.get("name");
        dragonTasksExtend.setEnabledFlag("TRUE");
        if ("ex".equals(name)){
            throw new Exception("Exception异常信息");
        }
        return Thread.currentThread().getName()+"hello "+name;
    }

    @RequestMapping("/static/hi")
    public String hi(@RequestBody Map map, DragonTasksExtend dragonTasksExtend)throws Exception{
        String name = (String)map.get("name");
        dragonTasksExtend.setEnabledFlag("TRUE");
        if ("ex".equals(name)){
            throw new Exception("Exception异常信息");
        }
        return Thread.currentThread().getName()+"hi "+name;
    }
    @PostMapping(value = "/test")
    public Response queryAll(){
        List<Map<String, Object>> maps = sw.buildQuery()
                .table("dragon_tasks_info")
                .eq("project_id", 322)
                .groupBegin()
                .conjuctionAnd()
                .and()
                .eq("task_state", "0")
                .eq("assignee", 56)
                .conjuctionOr()
                .and()
                .eq("task_state", "3")
                .eq("approver", 56)
                .groupEnd()
                .doQuery();
        return Response.ok();
    }
}
