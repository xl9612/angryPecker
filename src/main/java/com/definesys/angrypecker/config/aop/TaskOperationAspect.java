package com.definesys.angrypecker.config.aop;

import com.definesys.angrypecker.pojo.DragonTasks;
import com.definesys.angrypecker.pojo.DragonTasksExtend;
import com.definesys.angrypecker.properties.DragonConstants;
import com.definesys.angrypecker.properties.DragonProperties;
import com.definesys.angrypecker.properties.EmailProperties;
import com.definesys.angrypecker.properties.TaskConstants;
import com.definesys.angrypecker.service.DragonTaskService;
import com.definesys.angrypecker.service.DragonUserService;
import com.definesys.angrypecker.util.common.EmailUtils;
import com.definesys.angrypecker.util.user.userTask.UserTask;
import com.definesys.mpaas.common.http.Response;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Aspect
@Component
public class TaskOperationAspect {

    //任务实例对象，用于指定异步或者同步
    @Autowired
    private UserTask task;

    //邮件实例对象
    @Autowired
    private DragonProperties dragonProperties;

    @Autowired
    private DragonUserService userService;

    @Autowired
    private DragonTaskService dragonTaskService;

    private Logger log = LoggerFactory.getLogger(TaskOperationAspect.class);

    //操作任务
    @Pointcut("execution(public * com.definesys.angrypecker.controller.DragonTasksController.getOperationTask(..))")
    public void getOperationTask() {

    }

    //提交任务的切面
    @Pointcut("execution(public * com.definesys.angrypecker.controller.DragonTasksController.addDragonTasks(..))")
    public void addDragonTasks() {

    }

    //更新任务的切面
    @Pointcut("execution(public * com.definesys.angrypecker.controller.DragonTasksController.updateDragonTasks(..))")
    public void updateDragonTasks() {

    }

    /**
     * 方法执行之前
     * @param joinPoint
     */
    @Before("getOperationTask()")
    public void operationTaskBeforeLog(JoinPoint joinPoint) {
        log.info("方法执行前...");

    }

    //提交任务执行之后
    @After("addDragonTasks()")
    public void addDragonTasksAfterLog(JoinPoint joinPoint){
        log.info("提交任务执行之后");
    }

    //修改任务执行之后
    @After("updateDragonTasks() || addDragonTasks()")
    @Async
    public void updateDragonTasksAfterLog(JoinPoint joinPoint){
        DragonTasks paramTask = (DragonTasks)joinPoint.getArgs()[0];
        DragonTasks dragonTasks = dragonTaskService.getSendEmailTask(paramTask.getId(),paramTask.getModulech());
        dragonTasks.setCreatorUserIcon(paramTask.getSendler());
        dragonTasks.setSendler(paramTask.getSendler());
        List<Map<String,Object>> altsInfo = dragonTaskService.getAltsInfo(dragonTasks.getAlt(),dragonTasks.getProjectId());

        if (dragonTasks == null)
            return;
        if (DragonConstants.DRAGON_ENABLED_TRUE.equals(dragonTasks.getEnabledFlag())){
            tasksendEmail(dragonTasks,"");
        }
        log.info("updateDragonTasks修改任务执行:发送者:"+dragonTasks.getCreatorUserIcon()+"、"+dragonTasks.getSendler()+":修改任务执行之后收件人:"+dragonTasks.getHandler()+",收件邮箱:"+dragonTasks.getApproverEmail());
        if (altsInfo != null && altsInfo.size() > 0){
            //艾特人不为空,循环挨个发邮件通知
//            dragonTask.setApproverUserIcon(message+"审核");消息
            //To ->ApproverEmail
            String altHandler = "";
            String approverUserIcon = "@了您,需要您关注这个任务.";
            for (int i = 0;i<altsInfo.size();i++){

                if ("1".equals(altsInfo.get(i).get("isNotification"))) {
                    dragonTasks.setApproverUserIcon(approverUserIcon);
                    String altEmail = (String) altsInfo.get(i).get("loginEmail");
                    dragonTasks.setApproverEmail(altEmail);
                    altHandler = "<td style=\"padding:10px 35px;\"><strong>处理人： </strong>" + dragonTasks.getAssigneech() + "</td>\n";
                    tasksendEmail(dragonTasks, altHandler);
                    log.info("updateDragonTasks有@人,发给艾特人的:发送者:" + dragonTasks.getCreatorUserIcon() + "、" + dragonTasks.getSendler() + "发用给@人:" + (String) altsInfo.get(i).get("userName") + ",邮箱:" + dragonTasks.getApproverEmail());
                }
            }
        }

    }

    //操作任务执行之后
    @After("getOperationTask()")
    @Async
    public void operationTaskAfterLog(JoinPoint joinPoint){
        DragonTasksExtend dragonTasks = (DragonTasksExtend)joinPoint.getArgs()[0];
//        getCreatorUserIcon:设置指派人,不通过,指派,完成,再打开
        //AssigneeUserIcon处理人
        DragonTasks d = dragonTasks.getMyDragonTasks();
        if (d == null)
            return;
        tasksendEmail(d,"");
        log.info("getOperationTask:发送者:"+dragonTasks.getCreatorUserIcon()+"、"+dragonTasks.getSendler()
                +"方法执行后,接收者:"+d.getHandler()+",接收者邮箱:"+dragonTasks.getApproverEmail());


    }

    public void tasksendEmail(DragonTasks data,String altHandler) {
//        if (data != null && DragonConstants.DRAGON_ENABLED_TRUE.equalsIgnoreCase(data.getEnabledFlag())){
            EmailProperties email = dragonProperties.getEmail();
            String linkFailURL = email.getReturnServerUrl();
            //<strong style=\"color:#0892E1;margin-left:10px;\">"+d.getAssigneeUserIcon()+"</strong>
            email.setTo(data.getApproverEmail());
            email.setMsg(getMsg(data,altHandler,linkFailURL,email.getLogoUrl(),email.getReturnServerUrl(),"madpecker团队"));
            email.setSubject("Definesys-任务管理平台-任务提醒");
            EmailUtils.sendEmail(email,task);
            log.info(data.getApproverUserIcon()+":发送邮件方法执行,发给:"+data.getApproverEmail());

//        }
    }

    //返回
    @AfterReturning(returning="result",pointcut="getOperationTask()")
    public void doAfterReturning(Object result){
        result = (Response)result;
        log.info(((Response) result).getData()+",执行返回值：Code:"+((Response) result).getCode()+",Message:"+((Response) result).getMessage());
    }

    public void test(){
        log.info("发邮件方法");
    }

    //后置异常通知
    @AfterThrowing(throwing = "ex", pointcut = "getOperationTask()")
    public void throwss(JoinPoint jp, Exception ex){
        System.out.println("方法异常时执行.....");
    }

    public String getMsg(DragonTasks d,String altHandler,String linkFailURL,String logoUrl,String publicUrl,String teamUrl){

        String taskTypeColor = getTaskTypeOrPriorityColor(d.getTaskType());

        String taskStateColor = getTaskStateColor(d.getTaskState());

        String priorityColor = getTaskTypeOrPriorityColor(d.getPriority());

        String msg = " <table style=\"width:90%;text-align:left;border:1px solid #dcdee2;margin:0 auto;font-size:14px;color:#515a6e\">\n" +
                "        <thead><tr><td colspan=\"2\" style=\"text-align:left;border-bottom:1px solid #dcdee2;\"><img src=\""+logoUrl+"\" style=\"width:250px;\"></td></tr></thead>\n" +
                "        <tbody>\n" +
                "            <tr><td style=\"padding:10px 35px;\" colspan=\"2\">#"+d.getTaskProjectId()+" <span style=\"background:"+taskTypeColor+";padding:1px 5px;color:#ffffff;border-radius:3px;margin-left:10px;\">"+d.getTaskTypech()+"</span> <strong style=\"color:#0892E1;margin-left:10px;\">"+d.getTaskTitle()+"</strong></td></tr>\n" +
                "            <tr><td style=\"padding:10px 35px;\" colspan=\"2\"><strong style=\"color:#0892E1;margin-right:10px;\">"+d.getCreatorUserIcon()+"</strong>"+d.getApproverUserIcon()+"</td></tr>\n" +
                "            <tr>\n" +
                "                <td style=\"padding:10px 35px;\"><strong>项目： </strong>"+d.getProjectName()+" </td>\n" +
                "                <td style=\"padding:10px 35px;\"><strong>模块： </strong>"+d.getModulech()+"</td></tr>\n" +
                "            <tr>\n" +
                "                <td style=\"padding:10px 35px;\"><strong>工作量： </strong>"+d.getWorkload()+"</td>\n" +
                "                <td style=\"padding:10px 35px;\"><strong>环境： </strong>"+d.getEnvironmentTypech()+"</td>\n" +
                "                </tr>\n" +
                "            <tr>\n" +
                "                <td style=\"padding:10px 35px;\"><strong>状态： </strong><span style=\"background:"+taskStateColor+";padding:1px 5px;color:#ffffff;border-radius:3px;\">"+d.getTaskStatech()+"</span></td>\n" +
                "                <td style=\"padding:10px 35px;\"><strong>优先级： </strong><span style=\"background:"+priorityColor+";padding:1px 5px;color:#ffffff;border-radius:3px;\">"+d.getPrioritych()+"</span></td></tr>\n" +
                "            <tr>\n" +
                "                <td style=\"padding:10px 35px;\"><strong>计划日期： </strong>"+d.getPlannedStartDateStr()+"</td>\n" +
                "                <td style=\"padding:10px 35px;\"><strong>截止日期： </strong>"+d.getPlannedEndDateStr()+"</td></tr>\n" +
                "           <tr>\n" +
                "                    <td style=\"padding:10px 35px;\"><strong>创建人： </strong>"+d.getCreatorch()+"</td>\n" +altHandler+
        "                   </tr>\n" +
                "           <tr><td style=\"padding:10px 35px;\" colspan=\"2\"> "+d.getDescription()+"</td></tr>\n" +
                "        </tbody>\n" +
                "        <tfoot>\n" +
                "            <!-- <tr><td>屠龙团队</td></tr> -->\n" +
                "            <tr><td colspan=\"2\" style=\"padding:10px 25px;border-top:1px solid #dcdee2;background-color:#f8f8f9;\"><p style=\"margin: 1px 0;color:#808695\">"+teamUrl+"</p><a href=\""+publicUrl+"\" target=\"_blank\" style=\"color:#3385FF;\">"+linkFailURL+"</a></td></tr>\n" +
                "        </tfoot>\n" +
                "    </table>";

        return msg;
    }



    /**
     * 获取任务状态颜色值
     * @param taskState 任务状态
     * @return
     */
    private String getTaskStateColor(String taskState) {
        if ("0".equals(taskState)){
            return TaskConstants.COLOR_RED;
        }else if ("1".equals(taskState)){
            return TaskConstants.COLOR_PURPLE;
        }else if ("2".equals(taskState)){
            return TaskConstants.COLOR_BLUE;
        }else if ("3".equals(taskState)){
            return TaskConstants.COLOR_ORANGE;
        }else if ("4".equals(taskState)){
            return TaskConstants.COLOR_GREEN;
        }
        return null;
    }

    /**
     * 获得任务类型或者优先级的颜色值
     * @param taskType 传入的任务类型
     * @return
     */
    private String getTaskTypeOrPriorityColor(String taskType) {
        if ("0".equals(taskType)){
            return TaskConstants.COLOR_RED;
        }else if ("1".equals(taskType)){
            return TaskConstants.COLOR_ORANGE;
        }else if ("2".equals(taskType)){
            return TaskConstants.COLOR_BLUE;
        }else if ("3".equals(taskType)){
            return TaskConstants.COLOR_GREEN;
        }
        return null;
    }

}
