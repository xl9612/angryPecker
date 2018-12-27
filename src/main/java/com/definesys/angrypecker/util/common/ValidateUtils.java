package com.definesys.angrypecker.util.common;


import com.definesys.angrypecker.exception.DragonException;
import com.definesys.angrypecker.pojo.TimeCondition;

import java.util.List;

public class ValidateUtils {

    public static boolean checkIsNull(String property) {
        if (property == null || property.trim().length() == 0 || "null".equals(property)) {
            return true;
        }

        return false;
    }

    public static void checkIsNull(String property, String propertyName) {
        if (property == null || property.trim().length() == 0 || "null".equals(property)) {
            throw new DragonException(propertyName + "不能为空，请确认");
        }
    }

    //操作数据库失败/增删改
    public static void persistenceFailure(Integer integer,String msg) {
        if (integer < 0) {
            throw new DragonException(msg+"失败");
        }
    }
    //检测某个对象是否存在
    public static void checkIsExistence(String property, String mesg) {
        if (property == null || property.trim().length() == 0 || "null".equals(property)) {
            throw new DragonException(mesg);
        }
    }

    /**
     * TimeCondition对象判空（根据业务需求的判断规则）
     * @param condition
     * @return
     */
    public static boolean checkIsTimeConditionNull(TimeCondition condition){
        if(checkIsNull(condition.getTimeOperation())&&checkIsNull(condition.getStartDate())&&checkIsNull(condition.getEndDate()))
            return true;
        return false;
    }

    /**
     * list判空
     * @param list
     * @return
     */
    public static boolean checkIsCollectionNull(List list){
        if(list!=null&&list.size()>0){
            return false;
        }
        return true;
    }
}
