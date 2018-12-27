package com.definesys.angrypecker.util.common;

import com.definesys.angrypecker.properties.EmailProperties;
import com.definesys.angrypecker.util.user.userTask.UserTask;

import javax.mail.*;

/**
 * 邮件工具类
 */
public class EmailUtils {
    /**
     * 发送邮件
     * @param emailProperties 邮件对象
     * @param task  任务对象
     */
    public static void sendEmail(EmailProperties emailProperties, UserTask task) {
        task.sendEmail(emailProperties);
    }
    public static void sendAsyncEmail(EmailProperties emailProperties, UserTask task){
        task.sendAsyncEmail(emailProperties);
    }
    public static void sendEmailWithFile(EmailProperties emailProperties, UserTask task){
        task.sendEmailWithFile(emailProperties);
    }
}