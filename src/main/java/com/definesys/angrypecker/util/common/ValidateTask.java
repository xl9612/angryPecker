package com.definesys.angrypecker.util.common;

import com.definesys.angrypecker.properties.DragonConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 校验任务(task)操作状态判断
 */
public class ValidateTask {

    public static String validateTaskState(String taskState,String operation){
        //0未解决,1:已延期,2已关闭,3审批中,4已完成
        if (DragonConstants.TASK_HANDLER_OPERATION_ASSIGN.equals(operation)) {
            //指派:延期需要再打开才能指派
            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
                return "该任务不能再指派";
            }
        } else if (DragonConstants.TASK_HANDLER_OPERATION_DELAY.equals(operation)) {
            //延期
            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
                return "该任务不能再延期";
            }
        } else if (DragonConstants.TASK_HANDLER_OPERATION_CLOSE.equals(operation)) {
            //关闭
            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
                return "该任务不能执行关闭";
            }
        } else if (DragonConstants.TASK_HANDLER_OPERATION_OPEN_AGAIN.equals(operation)) {
            //再打开
            if (!"1".equals(taskState) || !"2".equals(taskState) || !"4".equals(taskState)){
                return "该任务不能执行再打开";
            }
        } else if (DragonConstants.TASK_HANDLER_OPERATION_COMPLETE.equals(operation)) {
            //完成:点完成就是指定审批人
            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
                return "该任务不能执行完成";
            }

        } else if (DragonConstants.TASK_HANDLER_OPERATION_ADOPT.equals(operation)) {
            //通过
            if ("2".equals(taskState)){
                return "该任务已关闭,不能执行通过操作";
            }

        } else if (DragonConstants.TASK_HANDLER_OPERATION_NOT_PASS.equals(operation)) {
            //不通过
            if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
                return "该任务不能执行完成";
            }

        }

        return null;
    }


    /**
     * 显示的按钮
     * 待解决（0）：项目拥有人、PM、PD、任务创建人：指派、延期、关闭；处理人:指派、完成；审核人:无
     * 待审核(3):项目拥有人、PM、PD：指派、通过、不通过、延期、关闭；创建人：指派、延期、关闭
     *      处理人：无；审核人：指派、通过、不通过
     * 已完成（4）/已延期（1）/已关闭（2）：项目拥有人、PD、PM、创建人：再打开；处理人：无；审核人：无
     * @return
     */
    public static List<String> getAllOperations(String taskState,Integer creator,Integer assignee,
                Integer approver, Integer userId,boolean isPdPmCreatorPro){
        List<String> operations = new ArrayList<>();
        //显示的按钮
        if (DragonConstants.TASK_STATE_HANDLER.equals(taskState)){
            //待解决
            if (isPdPmCreatorPro || userId == creator){

                operations.add(DragonConstants.TASK_HANDLER_OPERATION_ASSIGN);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_DELAY);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_CLOSE);
            }
            if (userId == assignee){
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_COMPLETE);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_ASSIGN);
            }
        }else if (DragonConstants.TASK_STATE_AUDITED.equals(taskState)){
            //待审核
            if (isPdPmCreatorPro){

                operations.add(DragonConstants.TASK_HANDLER_OPERATION_ASSIGN);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_DELAY);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_CLOSE);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_ADOPT);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_NOT_PASS);

            }
            if (userId == creator){
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_ASSIGN);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_DELAY);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_CLOSE);
            }

            if (userId == approver){
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_ASSIGN);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_ADOPT);
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_NOT_PASS);
            }

        }else if (DragonConstants.TASK_STATE_DELAYS.equals(taskState) || DragonConstants.TASK_STATE_CLOSES.equals(taskState) ||
                DragonConstants.TASK_STATE_COMPLETES.equals(taskState)){
            //状态:已完成、已延期、已关闭
            if (isPdPmCreatorPro||
                    userId == creator){
                operations.add(DragonConstants.TASK_HANDLER_OPERATION_OPEN_AGAIN);
            }

        }
        if (operations.size() > 0){
            Set set = new HashSet();
            set.addAll(operations);
            operations.clear();
            operations.addAll(set);
        }
        return operations;
    }

     /*if (DragonConstants.TASK_HANDLER_OPERATION_OPEN_AGAIN.equals(item.getOperation())){
        //再打开
        if (!"1".equals(taskState) && !"2".equals(taskState) && !"4".equals(taskState)){
            return Response.error("该任务不能执行再打开");
        }
    }else if (DragonConstants.TASK_HANDLER_OPERATION_COMPLETE.equals(item.getOperation())){
        //只有待解决才能完成
        if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState) || "3".equals(taskRowId)){
            return Response.error("该任务不能执行该操作:"+item.getOperation());
        }
    }else if (DragonConstants.TASK_HANDLER_OPERATION_NOT_PASS.equals(item.getOperation()) ||
            DragonConstants.TASK_HANDLER_OPERATION_ADOPT.equals(item.getOperation())){
        //通过不通过:审批中
        if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState) || "0".equals(taskRowId)){
            return Response.error("该任务不能执行该操作:"+item.getOperation());
        }
    }else {
        if ("1".equals(taskState) || "2".equals(taskState) || "4".equals(taskState)){
            return Response.error("该任务不能执行该操作:"+item.getOperation());
        }
    }*/
}
