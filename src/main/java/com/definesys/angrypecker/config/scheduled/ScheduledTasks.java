package com.definesys.angrypecker.config.scheduled;

import com.definesys.angrypecker.pojo.FndJwtToken;
import com.definesys.angrypecker.util.common.DragonJwtTokenUtils;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private MpaasQueryFactory sw;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 0 0 * * ?")
    public void reportCurrentTime() {
        DragonJwtTokenUtils jwtTokenUtil = new DragonJwtTokenUtils();
        List<FndJwtToken> fndJwtTokens = sw.buildQuery().doQuery(FndJwtToken.class);
        FndJwtToken fndJwtToken = null;
        String rowId = null;
        for (FndJwtToken jwtToken : fndJwtTokens){
            fndJwtToken = sw.buildQuery().eq("jwt_key", jwtToken.getKey()).doQueryFirst(FndJwtToken.class);
            rowId = jwtTokenUtil.getUsernameFromToken(fndJwtToken.getJwtToken());
            log.info(fndJwtTokens.size()+"||"+rowId);
            if (rowId == null){
                sw.buildQuery().bind(FndJwtToken.class).eq("jwt_key",fndJwtToken.getKey()).doDelete();
                log.info("The time is now {},删除:"+fndJwtToken.getKey(), dateFormat.format(new Date()));
            }
        }


    }
}
//作者：方志朋
//来源：CSDN
//原文：https://blog.csdn.net/forezp/article/details/71023783
//版权声明：本文为博主原创文章，转载请附上博文链接！