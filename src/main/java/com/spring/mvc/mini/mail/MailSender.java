package com.spring.mvc.mini.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class MailSender {

    private static final Logger LOG = LoggerFactory.getLogger(MailSender.class);

    @Value("${mail.starttls.enable}")
    private String starttlsEnable;

    @Value("${mail.auth}")
    private String auth;

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private String port;

    /**
     * session为一个基本的邮件会话，通过该会话可执行其他邮件工作
     * 如：之后的初始化一个MimeMessage实例
     */
    private transient Session session;

    public void sendMail(final String fromAddress,final String password, Address[] toAddress, String subject, String text) throws Exception {

            Authenticator au =  new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromAddress, password);
                }
              };

        session = Session.getInstance(getProperties(), au);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));//设置发件人地址
        message.setSubject(subject);//设置邮件主题
        message.setText(text);
        message.setRecipients(Message.RecipientType.TO, toAddress);//设置收件人的邮箱地址，toAddress可包含多个地址，表示群发

        // 根据session来获得一个Transport抽象类对象
        Transport tran = session.getTransport("smtp");

        // 连接发信人邮件服务器（ 邮箱服务器地址，端口号，发件箱用户名，发件箱密码）
        tran.connect(host,Integer.parseInt(port), fromAddress, password);

        // ps：toAddress不能省略，否则会导致报空指针异常。因为在SMTPTransport的sendMessage实现类中不会自动从message中获取Recipients的内容。
        tran.sendMessage(message, toAddress);

        // 关闭通道
        tran.close();

        LOG.info("Send Mail Done: " + fromAddress + " to" + toAddress.toString());

    }

    private java.util.Properties getProperties() {
        java.util.Properties props = new java.util.Properties();

        props.put("mail.smtp.starttls.enable", starttlsEnable);//设置是否使用STARTTLS命令，将纯文本连接升级为加密连接
        props.put("mail.smtp.auth", auth);//设置是否需要验证
        props.put("mail.smtp.host", host);//设置服务器地址
        props.put("mail.smtp.port", port);//设置服务器端口
        return props;
    }

}
