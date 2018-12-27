package com.definesys.angrypecker.util.user.userTask;

import com.definesys.angrypecker.properties.EmailProperties;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 任务抽象类，一些常用的操作可以抽象为一个任务对象
 * 如向某一地址发送邮件这个操作就可以抽象为一个任务对象
 * 定义这个类的初衷是为了实现后台耗时操作与即使前端响应之间矛盾的解决方式即（异步处理）。
 */
@Component
public class UserTask {
    /**
     * 获取会话对象
     *
     * @param emailProperties
     * @return 返回会话对象
     */
    public Session getSession(EmailProperties emailProperties) {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host",emailProperties.getHost());
        properties.setProperty("mail.transport.protocol",emailProperties.getProtocol());
        properties.setProperty("mail.smtp.port", emailProperties.getPort());
        properties.setProperty("mail.smtp.auth","true");
        MailSSLSocketFactory msf = null;
        //当不能建立链接时，抛出异常。
        try {
            msf = new MailSSLSocketFactory();
            msf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            throw new MpaasBusinessException("发送邮件失败！");
        }
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", msf);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProperties.getAccount(), emailProperties.getPassword());
            }
        });
        //获取session失败时，抛出业务异常。
        if (session == null) {
            throw new MpaasBusinessException("发送邮件异常！");
        }
        return session;
    }

    /**
     * 创建普通文本邮件
     * @param session   会话
     * @param emailProperties   邮件信息
     * @return  返回邮件消息对象
     */
    public Message createMessage(Session session,EmailProperties emailProperties) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(emailProperties.getFrom());
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailProperties.getTo()));
            message.setSubject(emailProperties.getSubject());
            message.setContent(emailProperties.getMsg(), "text/html;charset=UTF-8");
        } catch (AddressException e) {
            throw new MpaasBusinessException("地址有误");
        } catch (MessagingException e) {
            throw new MpaasBusinessException("邮件构建出错");
        }
        return message;
    }

    /**
     * 构建一个带附件的邮件
     * @param session   会话
     * @param emailProperties   邮件信息
     * @return 返回一个带附件的邮件信息
     */
    public Message createAttachMessage(Session session,EmailProperties emailProperties) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(emailProperties.getFrom());
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailProperties.getTo()));
            message.setSubject(emailProperties.getSubject());
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(emailProperties.getMsg(), "text/html;charset=UTF-8");
            String[] attacheFilePath = emailProperties.getAttacheFilePath();
            List<MimeBodyPart> partList = new ArrayList<MimeBodyPart>();
            if(attacheFilePath!=null&&attacheFilePath.length>0){
                for (String path:attacheFilePath
                ) {
                    File file = new File(path);
                    if(!file.exists())
                        continue;
                    MimeBodyPart att = new MimeBodyPart();
                    DataSource dataSource = new FileDataSource(new File(path));
                    att.setDataHandler(new DataHandler(dataSource));
                    att.setFileName(dataSource.getName());
                    partList.add(att);
                }
            }
            MimeMultipart mix = new MimeMultipart("mixed");
            mix.addBodyPart(text);
            if(partList.size()>0){
                for (MimeBodyPart part:partList
                ) {
                    mix.addBodyPart(part);
                }
            }
            message.setContent(mix);
            message.saveChanges();
            message.writeTo(new FileOutputStream("d:/upload/user/mail.eml"));
        }catch (Exception e) {
            throw new MpaasBusinessException("邮件错误");
        }
        return message;
    }

    /**
     * 获取邮件发送对象
     * @param session   会话
     * @return  返回一个发送对象
     */
    public Transport getTransport(Session session){
        Transport ts = null;
        try {
            ts = session.getTransport();
        } catch (NoSuchProviderException e) {
            throw new MpaasBusinessException("邮件发送错误");
        }
        return ts;
    }

    /**
     * 发送一个文本邮件
     * @param emailProperties
     */
//    @Async
    public void sendEmail(EmailProperties emailProperties) {
        Session session = getSession(emailProperties);
        //session.setDebug(true);
        Transport ts = getTransport(session);
        Message message = null;
        try {
            ts.connect(emailProperties.getHost(), emailProperties.getAccount(), emailProperties.getPassword());
            message = createMessage(session,emailProperties);
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Async
    public void sendAsyncEmail(EmailProperties emailProperties) {
        Session session = getSession(emailProperties);
        //session.setDebug(true);
        Transport ts = getTransport(session);
        Message message = null;
        try {
            ts.connect(emailProperties.getHost(), emailProperties.getAccount(), emailProperties.getPassword());
            message = createMessage(session,emailProperties);
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param emailProperties
     */
    @Async
    public void sendEmailWithFile(EmailProperties emailProperties) {
        Session session = getSession(emailProperties);
        //session.setDebug(true);
        Transport ts = getTransport(session);
        Message message = null;
        try {
            ts.connect(emailProperties.getHost(), emailProperties.getAccount(), emailProperties.getPassword());
            message = createAttachMessage(session,emailProperties);
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        } catch (MessagingException e) {
            new MpaasBusinessException("发送邮件出错");
        }
    }
}
