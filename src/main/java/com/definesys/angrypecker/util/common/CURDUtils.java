package com.definesys.angrypecker.util.common;

import com.definesys.angrypecker.pojo.DragonProjectModuleInfo;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.query.MpaasQuery;
import com.definesys.mpaas.query.model.MpaasBasePojo;

public class CURDUtils {

    /**
     * 更新记录
     *
     * @param bindObject 绑定的对象
     * @param rowId      记录唯一标识
     * @author xulei
     */
    public static void update(MpaasQuery query, MpaasBasePojo bindObject, String rowId, String... strList) {
        query.bind(bindObject)
                .addRowIdClause("id", "=", rowId)
                .update(strList)
                .doUpdate();
    }

    /**
     * 用于在创建模块和更新模块信息时判断模块名是否重复
     * @param query
     * @param item
     */
    public static void isModuleNameExist(MpaasQuery query, DragonProjectModuleInfo item) {
        DragonProjectModuleInfo dragonProjectModuleInfo = query.sql("select * from dragon_project_module_info")
                .and()
                .eq("project_id", item.getProjectId())
                .eq("module_name", item.getModuleName())
                .doQueryFirst(DragonProjectModuleInfo.class);

        if (dragonProjectModuleInfo != null&&!dragonProjectModuleInfo.getRowId().equals(item.getRowId()))
            throw new MpaasBusinessException("该模块名称已存在");
    }
    public static void isLoginMailExist(MpaasQuery query, DragonUser user){
        DragonUser dragonUser = query.sql("select * from fnd_users")
                .eq("login_email", user.getLoginEmail())
                .doQueryFirst(DragonUser.class);
        if(dragonUser !=null && !dragonUser.getRowId().equals(user.getRowId()))
            throw new MpaasBusinessException("该邮箱已注册");
    }
}
