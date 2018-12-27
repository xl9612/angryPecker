package com.definesys.angrypecker.properties;

public class DragonConstants {

    /**
     * 默认头像
     */
    public static final String  DEFAULT_USER_INCON = "615283b1-814e-4dc7-8ca4-03b5e650a20f.png";

    /**
     * 任务主页具体操作
     * :未解决、待审核、已解决、已关闭
     */
    public static final String TASK_OPERATION_EXPORT = "export";//导出
    public static final String TASK_OPERATION_ACTIVITYPROBLEM = "activityProblem";//活动问题:任务状态是:未解决、待审核
    public static final String TASK_OPERATION_ALLPROBLEMS = "allProblems";//所有问题；所有任务状态
    public static final String TASK_OPERATION_MYTODO = "myToDo";//我的待办
    public static final String TASK_OPERATION_ASSIGNEDTOME = "assignedToMe";//分配给我
    public static final String TASK_OPERATION_MYDISTRIBUTION = "myDistribution";//我的分配
    public static final String TASK_OPERATION_ALTMY = "altMy";//@(alt)我的
    public static final String TASK_OPERATION_ACTIVITYPROBLEM_NAME = "活动问题";
    public static final String TASK_OPERATION_ALLPROBLEMS_NAME = "所有问题";//所有问题；所有任务状态
    public static final String TASK_OPERATION_MYTODO_NAME = "我的待办";//我的待办
    public static final String TASK_OPERATION_ASSIGNEDTOME_NAME = "分配给我";//分配给我
    public static final String TASK_OPERATION_MYDISTRIBUTION_NAME = "我的分配";//我的分配

    /**
     * 任务主页_时间
     */
    public static final String TASK_TIME_PARSETIME = "yyyy-MM-dd HH:mm:ss";//日期格式
    public static final String TASK_TIME_PARSEDATE = "yyyy-MM-dd";//日期格式
    public static final String TASK_TIME_TODAY = "today";//今天
    public static final String TASK_TIME_YESTERDAY = "yesterday";//昨天
    public static final String TASK_TIME_LASTTHREEDAY = "lastThreeDay";//最近三天
    public static final String TASK_TIME_WEEK = "week";//本周
    public static final String TASK_TIME_LASTWEEK = "lastWeek";//上周
    public static final String TASK_TIME_MONTH = "month";//本月
    public static final String TASK_TIME_SELECTIONRANGE = "selectionRange";//


    //默认未授权的地址
//    public static final String DEFAULT_UNAUTHENTICATION_URL = "/authentication/require";

    //认证_发送邮箱中得地址
    public static final String AUTH_EMAIL_URL = "http://hr.definesys.com/dragon/dev/#/verifyemail";

    /**
     * 任务(task)中的具体操作
     * 指派、延期、关闭、再打开、完成、通过、不通过
     */
    public static final String TASK_HANDLER_OPERATION_ASSIGN = "assign";//指派

    public static final String TASK_HANDLER_OPERATION_DELAY = "delay";//延期

    public static final String TASK_HANDLER_OPERATION_CLOSE = "close";//关闭

    public static final String TASK_HANDLER_OPERATION_OPEN_AGAIN = "openAgain";//再打开

    public static final String TASK_HANDLER_OPERATION_COMPLETE = "complete";//完成

    public static final String TASK_HANDLER_OPERATION_ADOPT = "adopt";//通过

    public static final String TASK_HANDLER_OPERATION_NOT_PASS = "notPass";//不通过

    /**
     * 任务状态
     */
    public static final String TASK_STATE_HANDLER = "0";//待解决
    public static final String TASK_STATE_DELAYS = "1";//已延期
    public static final String TASK_STATE_CLOSES = "2";//已关闭
    public static final String TASK_STATE_AUDITED = "3";//待审核
    public static final String TASK_STATE_COMPLETES = "4";//已完成

    /**
     * 开启的标识
     */
    public static final String DRAGON_ENABLED_TRUE = "TRUE";//开启FALSE
    public static final String DRAGON_ENABLED_FALSE = "FALSE";//关闭FALSE

    /**
     * 任务发邮件出去的类型
     */
    public static final String TASK_EMAIL_OPERATIONTASK = "operationTask";//操作任务

}
