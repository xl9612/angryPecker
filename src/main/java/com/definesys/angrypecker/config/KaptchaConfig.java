package com.definesys.angrypecker.config;//package com.definesys.dragon.config;
//
//import com.google.code.kaptcha.impl.DefaultKaptcha;
//import com.google.code.kaptcha.util.Config;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Properties;
//
//@Configuration
//public class KaptchaConfig {
//    @Bean
//    public DefaultKaptcha producer() {
//        Properties properties = new Properties();
//        properties.put("kaptcha.border", "no");
//        properties.put("kaptcha.textproducer.font.color", "black");
//        properties.put("kaptcha.textproducer.char.space", "6");
//        properties.put("kaptcha.textproducer.char.length", "4");
//        properties.put("kaptcha.image.height", "34");
//        properties.put("kaptcha.textproducer.font.size", "25");
//
//        properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
//        Config config = new Config(properties);
//        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
//        defaultKaptcha.setConfig(config);
//        return defaultKaptcha;
//    }
//}
