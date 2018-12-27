package com.definesys.angrypecker.service;

import com.definesys.angrypecker.exception.DragonException;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.properties.DragonProperties;
import com.definesys.angrypecker.properties.EmailProperties;
import com.definesys.angrypecker.util.common.EmailUtils;
import com.definesys.angrypecker.util.user.userTask.UserTask;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DragonUserService {
    //倚天工具实例对象
    @Autowired
    private MpaasQueryFactory sw;
    //倚天工具日志打印实例对象
    @Autowired
    private SWordLogger logger;
    //邮件实例对象
    @Autowired
    private DragonProperties dragonProperties;
    //任务实例对象，用于指定异步或者同步
    @Autowired
    private UserTask task;


    /**
     * 发送邮件，如果邮箱以注册，发送重置密码邮件，返回成功
     * 未注册返回“邮箱未注册”。
     * @param emailStr 待验证邮箱
     * @return 返回验证结果
     */
    public void sendResetPwdEmail(String emailStr,String compact){
        EmailProperties email = dragonProperties.getEmail();
        String clickURL = email.getReturnServerUrl()+"#/resetPassword?email="+emailStr+"&token="+compact;
        String linkFailURL = email.getReturnServerUrl()+"#/findpassword";
        email.setTo(emailStr);
        email.setTeamName("madpecker团队");
        email.setMsg("<table style=\"width:50%;text-align:left;border:1px solid #dcdee2;margin:0 auto;\">" +
                "<thead>" +
                    "<tr>" +
                        "<td style=\"text-align:left;border-bottom:1px solid #dcdee2;\">" +
                            "<img src=\""+email.getLogoUrl()+"\" style=\"width:250px;\">" +
                        "</td>" +
                    "</tr>" +
                "</thead>" +
                "<tbody>" +
                    "<tr>" +
                        "<td style=\"padding:10px 25px;font-size: 14px;\">亲爱的<a href=\"javacript:void(0);\" style=\"color:#3385FF;\">"+emailStr+"</a></td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"padding:10px 25px;font-size: 14px;\">您好！</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"padding:10px 25px;font-size: 14px;\">您在屠龙系统个人中心申请找回密码，请点击下面的按钮来重新设定密码</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"padding:10px 25px;\">" +
                            "<a href=\""+clickURL+"\" target=\"_blank\" style=\"color:#ffffff;font-size:16px;padding:5px 25px;display:block;background-color:#3385FF;border:none;border-radius: 3px;box-shadow:0px 0px 5px #3385FF;width:65px;\">重置密码</a>" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"padding:10px 25px;font-size: 14px;\">如果无法点击，请复制以下地址到浏览器，然后直接打开</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"padding:10px 25px;font-size: 14px;\">" +
                            "<a href=\""+clickURL+"\" style=\"color:#3385FF;font-size: 14px;\">"+clickURL+"</a>" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"padding:10px 25px;font-size: 14px;\">以上连接两小时内有效" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"padding:10px 25px;font-size: 14px;\">如果该连接失效，请你访问网站<a href=\""+linkFailURL+"\" style=\"color:#3385FF;\">"+linkFailURL+"</a>重新申请找回密码</td></tr></tbody><tfoot><tr><td style=\"padding:10px 25px;border-top:1px solid #dcdee2;background-color:#f8f8f9;font-size: 14px;\"><p style=\"margin: 1px 0;color:#808695\">"+email.getTeamName()+"</p><a href=\""+email.getReturnServerUrl()+"\" target=\"_blank\" style=\"color:#3385FF;\">"+email.getReturnServerUrl()+"</a></td></tr></tfoot></table>");

        email.setSubject("Definesys-任务管理平台-找回密码");
        EmailUtils.sendAsyncEmail(email,task);
    }

    /**
     * 邮箱认证
     * @param emailStr
     */
    public void sendCertificationEmail(String emailStr,String compact){
        EmailProperties email = dragonProperties.getEmail();
        String clickURL = email.getReturnServerUrl() + "#/verifyemail?email=" + emailStr + "&token=" + compact;
        String linkFailURL = email.getReturnServerUrl() + "#/login";
        email.setTo(emailStr);
        email.setTeamName("madpecker团队");
        email.setMsg("<table style=\"width:50%;text-align:left;border:1px solid #dcdee2;margin:0 auto;\"><thead><tr><td style=\"text-align:left;border-bottom:1px solid #dcdee2;\"><img src=\""+email.getLogoUrl()+"\" style=\"width:250px;\"></td></tr></thead><tbody><tr><td style=\"padding:10px 25px;font-size: 14px;\">亲爱的<a href=\"javacript:void(0);\" style=\"color:#3385FF;\">"+emailStr+"</a></td></tr><tr><td style=\"padding:10px 25px;font-size: 14px;\">您好！</td></tr><tr><td style=\"padding:10px 25px;font-size: 14px;\">您申请使用此电子邮箱访问您的屠龙账户。</td></tr><tr><td style=\"padding:10px 25px;font-size: 14px;\">点击下方链接验证电子邮件地址。</td></tr><tr><td style=\"padding:10px 25px;\"><a href=\""+clickURL+"\" target=\"_blank\" style=\"color:#ffffff;font-size:16px;padding:5px 25px;display:block;background-color:#3385FF;border:none;border-radius: 3px;box-shadow:0px 0px 5px #3385FF;width:65px;\">验证邮箱</a></td></tr><tr><td style=\"padding:10px 25px;font-size: 14px;\">如果无法点击，请复制以下地址到浏览器，然后直接打开</td></tr><tr><td style=\"padding:10px 25px;font-size: 14px;\"><a href=\""+clickURL+"\" style=\"color:#3385FF;font-size: 14px;\">"+clickURL+"</a></td></tr><tr><td style=\"padding:10px 25px;font-size: 14px;\">以上连接两小时内有效</td></tr><tr><td style=\"padding:10px 25px;font-size: 14px;\">如果该连接失效，请你访问网站<a href=\""+linkFailURL+"\" style=\"color:#3385FF;\">"+linkFailURL+"</a>重新验证</td></tr></tbody><tfoot><tr><td style=\"padding:10px 25px;border-top:1px solid #dcdee2;background-color:#f8f8f9;font-size: 14px;\"><p style=\"margin: 1px 0;color:#808695\">"+email.getTeamName()+"</p><a href=\""+email.getReturnServerUrl()+"\" target=\"_blank\" style=\"color:#3385FF\">"+email.getReturnServerUrl()+"</a></td></tr></tfoot></table>");
        email.setSubject("验证您的屠龙账户电子邮件地址");
        EmailUtils.sendAsyncEmail(email,task);
    }

    /**
     * 发送邀请邮件
     * @param inviteMail
     * @param compact
     */
    public void sendInviteEmail(String inviteMail,String loginName,String loginMail,String projectName,String compact){
        EmailProperties email = dragonProperties.getEmail();
        String clickURL = email.getReturnServerUrl() + "#/register?inviteMail="+ inviteMail+ "&token=" + compact;
        String linkFailURL = email.getReturnServerUrl() + "#/register";
        email.setTo(inviteMail);
        email.setTeamName("madpecker团队");
        email.setMsg("<table style=\"width:90%;text-align:left;border:1px solid #dcdee2;margin:0 auto;font-size:14px;\"><thead><tr><td style=\"text-align:left;border-bottom:1px solid #dcdee2;\"><img src=\""+email.getLogoUrl()+"\" style=\"width:250px;\"></td></tr></thead><tbody><tr><td style=\"padding:10px 25px;\">亲爱的<a href=\"javacript:void(0);\" style=\"color:#3385FF;\">"+inviteMail+"</a></td></tr><tr><td style=\"padding:10px 25px;\">您好！</td></tr><tr><td style=\"padding:10px 25px;\">屠龙用户"+loginName+"("+loginMail+")邀请您加入项目["+projectName+"],请点击下面的按钮来注册</td></tr><tr><td style=\"padding:10px 25px;\"><a href=\""+clickURL+"\" target=\"_blank\" style=\"color:#ffffff;font-size:16px;padding:5px 25px;display:block;background-color:#3385FF;border:none;border-radius: 3px;box-shadow:0px 0px 5px #3385FF;width:65px;\">同意加入</a></td></tr><tr><td style=\"padding:10px 25px;\">如果无法点击，请复制以下地址到浏览器，然后直接打开</td></tr><tr><td style=\"padding:10px 25px;\"><a href=\"javacript:void(0);\" style=\"color:#3385FF;\">"+clickURL+"</a></td></tr><tr><td style=\"padding:10px 25px;\">以上连接48小时内有效</td></tr><tr><td style=\"padding:10px 25px;\">如果该连接失效，请你访问网站<a href=\"http://www.definesys.com\" style=\"color:#3385FF;\">"+linkFailURL+"</a>并注册</td></tr></tbody><tfoot><tr><td style=\"padding:10px 25px;border-top:1px solid #dcdee2;background-color:#f8f8f9;\"><p style=\"margin: 1px 0;color:#808695\">"+email.getTeamName()+"</p><a href=\""+email.getReturnServerUrl()+"\" target=\"_blank\" style=\"color:#3385FF;\">"+email.getReturnServerUrl()+"</a></td></tr></tfoot></table>");
        email.setSubject("屠龙提醒您收到项目邀请");
        EmailUtils.sendAsyncEmail(email,task);
    }

    /**
     * 发送修改邮箱验证邮件
     * @param rowId
     * @param newEmail
     * @param compact
     */
    public void sendModifyEmail(String rowId,String newEmail,String compact){
        EmailProperties email = dragonProperties.getEmail();
        String clickURL = email.getReturnServerUrl() + "#/verifyemail?rowId=" + rowId+ "&newEmail=" + newEmail + "&token=" + compact;
        //String linkFailURL = email.getReturnServerUrl() + "#/verifyemail";
        email.setTo(newEmail);
        email.setTeamName("madpecker团队");
        email.setMsg("<table style=\"width:90%;text-align:left;border:1px solid #dcdee2;margin:0 auto;font-size:14px;\"><thead><tr><td style=\"text-align:left;border-bottom:1px solid #dcdee2;\"><img src=\""+email.getLogoUrl()+"\" style=\"width:250px;\"></td></tr></thead><tbody><tr><td style=\"padding:10px 25px;\">亲爱的<a href=\"javacript:void(0);\" style=\"color:#3385FF;\">"+newEmail+"</a></td></tr><tr><td style=\"padding:10px 25px;\">您好！</td></tr><tr><td style=\"padding:10px 25px;\">您申请将原电子邮件修改为此电子邮件访问您的屠龙账户</td></tr><tr><td style=\"padding:10px 25px;\">点击下方按钮验证邮件地址</td></tr><tr><td style=\"padding:10px 25px;\"><a href=\""+clickURL+"\" target=\"_blank\" style=\"color:#ffffff;font-size:16px;padding:5px 25px;display:block;background-color:#3385FF;border:none;border-radius: 3px;box-shadow:0px 0px 5px #3385FF;width:65px;\">验证邮箱</a></td></tr><tr><td style=\"padding:10px 25px;\">如果无法点击，请复制以下地址到浏览器，然后直接打开</td></tr><tr><td style=\"padding:10px 25px;\"><a href=\"javacript:void(0);\" style=\"color:#3385FF;\">"+clickURL+"</a></td></tr><tr><td style=\"padding:10px 25px;\">以上连接2小时内有效</td></tr><tr><td style=\"padding:10px 25px;\">如果该连接失效，请您重新提交邮箱修改</td></tr></tbody><tfoot><tr><td style=\"padding:10px 25px;border-top:1px solid #dcdee2;background-color:#f8f8f9;\"><p style=\"margin: 1px 0;color:#808695\">"+email.getTeamName()+"</p><a href=\""+email.getReturnServerUrl()+"\" target=\"_blank\" style=\"color:#3385FF;\">"+email.getReturnServerUrl()+"</a></td></tr></tfoot></table>");
        email.setSubject("修改邮箱验证");
        EmailUtils.sendAsyncEmail(email,task);
    }

    /**
     * 获得当前用户信息
     * 只有经过Token的调次接口才会有值
     */
    public DragonUser getDragonUser(){
        DragonUser dragonUser = null;
        try {
            dragonUser = (DragonUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (Exception e){
            throw new DragonException("Token信息失效");
        }
        return dragonUser;
    }

    /**
     * 验证某一用户是否开启邮件通知
     * @param userId 用户id
     * @param projectId 该用户所在项目id
     * @return 如果开启，返回true，否则返回fale
     */
    public boolean sendMailCheck(Integer userId,Integer projectId){
        Map<String, Object> isNotificationMap = sw.buildQuery()
                .sql("select is_notification from dragon_user_project_info")
                .eq("user_id", userId)
                .eq("project_id", projectId)
                .doQueryFirst();
        if(isNotificationMap==null)
            return false;
        Object isNotificationObj = isNotificationMap.get("is_notification");
        if(isNotificationObj==null||"".equals(isNotificationObj.toString()))
            return false;
        Boolean is_notfication = Boolean.valueOf(isNotificationObj.toString());
        return is_notfication;
    }

}
