package com.definesys.angrypecker.service;

import com.definesys.angrypecker.pojo.DragonProjectModuleInfo;
import com.definesys.angrypecker.pojo.DragonTasks;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class DragonModuleService {
    /**
     * 删除模块，同时删除该模块中包含的bug
     * @param rowId 模块唯一标识
     */
    @Transactional
    public void deleteModule(MpaasQueryFactory sw, String rowId){
        //查询该模块的rowId获取模块id
        Map<String, Object> map = sw.buildQuery().sql("select id from dragon_project_module_info")
                .addRowIdClause("id", "=", rowId)
                .doQueryFirst();
        //删除该模块
        sw.buildQuery().bind(DragonProjectModuleInfo.class)
                .addRowIdClause("id", "=", rowId)
                .doDelete();
        //删除该模块包含的所有bug
        sw.buildQuery().bind(DragonTasks.class)
                .and()
                .eq("module_id",map.get("id"))
                .doDelete();
    }
}
