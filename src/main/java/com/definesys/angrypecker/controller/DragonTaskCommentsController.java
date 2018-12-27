package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonTaskComments;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.service.DragonUserService;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.query.MpaasQueryFactory;
import com.definesys.mpaas.query.conf.MpaasQueryConfig;
import com.definesys.mpaas.query.util.MpaasQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: 徐磊
 * @since: 2018-12-17
 * @history: 1.2018-12-17 created by 徐磊
 */
@RestController
@RequestMapping(value = "/api/comments")
public class DragonTaskCommentsController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private MpaasQueryConfig config;

    @Autowired
    private DragonUserService dragonUserService;

    /**
     * 获取指定task的所有讨论内容
     * @param taskRowId task唯一标识
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Response queryDragonTaskComments(@RequestBody Map<String,String> taskRowId) {
        if(ValidateUtils.checkIsNull(taskRowId.get("rowId")))
            throw new MpaasBusinessException("不存在该task");
        String rowIdStr = MpaasQueryUtil.decryptRowId(taskRowId.get("rowId"), config.rowIdSecret);
        Integer rowIdInt = Integer.valueOf(rowIdStr);
        List<DragonTaskComments> dragonTaskCommentsList = sw.buildQuery()
                .sql("select tc.id,tc.task_id,comments,submit_time,tc.user_id,(select user_name from fnd_users u where tc.user_id = u.id) userName,(select user_icon from fnd_users u where tc.user_id = u.id) userIcon from dragon_task_comments tc ")
                .eq("task_id", rowIdInt)
                .doQuery(DragonTaskComments.class);
        return Response.ok().table(dragonTaskCommentsList);
    }

    /**
     *
     * @param comments
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response addDragonTaskComments(@RequestBody DragonTaskComments comments) {
        if(comments==null||ValidateUtils.checkIsNull(comments.getTaskId().toString()))
            throw new MpaasBusinessException("不存在该task");
        DragonUser dragonUser = (DragonUser) dragonUserService.getDragonUser();
        comments.setUserId(dragonUser.getId());
        comments.setSubmitTime(new Date());
        Object key = sw.buildQuery()
                .bind(comments)
                .doInsert(comments);
        return Response.ok();
    }

}