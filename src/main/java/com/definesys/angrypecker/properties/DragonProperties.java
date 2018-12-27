package com.definesys.angrypecker.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 屠龙动态配置相关类
 */
@Component
@ConfigurationProperties(DragonProperties.PREFIX)
public class DragonProperties {
    public static final String PREFIX = "com.define.dragon";

    public static final String PATH = "";//路径

    private EmailProperties email = new EmailProperties();

    private FileProperties file = new FileProperties();

    public EmailProperties getEmail() {
        return email;
    }

    public void setEmail(EmailProperties email) {
        this.email = email;
    }

    public FileProperties getFile() {
        return file;
    }

    public void setFile(FileProperties file) {
        this.file = file;
    }
}
