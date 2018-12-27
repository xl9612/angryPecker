package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonTaskLogs;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: wang
 * @since: 2018-11-16
 * @history: 1.2018-11-16 created by wang
 */
@RestController
@RequestMapping(value = "/api/taskLogs")
public class DragonTaskLogsController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response queryDragonTaskLogs() {
        List<DragonTaskLogs> table = sw.buildQuery()
                .doQuery(DragonTaskLogs.class);
        return Response.ok().table(table);
    }

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    public Response pageQueryDragonTaskLogs(@RequestParam(value = "page") Integer page,
                                            @RequestParam(value = "pageSize") Integer pageSize) {
        return sw.buildQuery()
                .doPageQuery(page, pageSize, DragonTaskLogs.class)
                .httpResponse();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response addDragonTaskLogs(@RequestBody DragonTaskLogs item) {
        Object key = sw.buildQuery()
                .bind(item)
                .doInsert();
        return Response.ok().data(key);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Response deleteDragonTaskLogs(@RequestParam(value = "rowId") String rowId) {
        sw.buildQuery()
                .bind(DragonTaskLogs.class)
                .addRowIdClause("id", "=", rowId)
                .doDelete();
        return Response.ok();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response updateDragonTaskLogs(@RequestBody DragonTaskLogs item) {
        sw.buildQuery()
                .addRowIdClause("id", "=", item.getRowId())
                .doUpdate(item);
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
        sw.buildQuery("dragontasklogs_v")
                .fileName("xlt.xlsx")
                .doExport(response, DragonTaskLogs.class);
    }

}