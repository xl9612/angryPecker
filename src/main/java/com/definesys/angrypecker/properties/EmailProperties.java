package com.definesys.angrypecker.properties;

import java.util.Date;

/**
 * 邮寄配置类，该类包含一封邮件的必要信息
 */
public class EmailProperties {
    //发送端邮箱
    private String account;
    //邮箱密码
    private String password;
    //邮件发送方名字
    private String from;
    //接收方邮箱
    private String to;
    //邮箱服务器
    private String host;
    //服务器端口
    private String port;
    //传输协议
    private String protocol;
    //邮件内容
    private String msg;
    //邮件中附带图片路径
    private String[] imgPath;
    //邮件附件路径
    private String[] attacheFilePath;
    //邮件主题
    private String subject;
    //发送时间
    private Date sendDate;
    //链接失效之后访问地址
    private String linkFailURL;
    //邮件内部点击按钮地址
    private String clickURL;
    //邮件内部点击返回服务器地址
    private String returnServerUrl;
    //邮件logo地址
    private String logoUrl;
    //屠龙产品团队名称
    private  String teamName;

    public String getLinkFailURL() {
        return linkFailURL;
    }

    public void setLinkFailURL(String linkFailURL) {
        this.linkFailURL = linkFailURL;
    }

    public String getClickURL() {
        return clickURL;
    }

    public void setClickURL(String clickURL) {
        this.clickURL = clickURL;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String[] getImgPath() {
        return imgPath;
    }
    public void setImgPath(String[] imgPath) {
        this.imgPath = imgPath;
    }

    public String[] getAttacheFilePath() {
        return attacheFilePath;
    }

    public void setAttacheFilePath(String[] attacheFilePath) {
        this.attacheFilePath = attacheFilePath;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getSendDate() {
        if(this.sendDate==null){
           this.sendDate = new Date();
        }
        return this.sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getReturnServerUrl() {
        return returnServerUrl;
    }

    public void setReturnServerUrl(String returnServerUrl) {
        this.returnServerUrl = returnServerUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
