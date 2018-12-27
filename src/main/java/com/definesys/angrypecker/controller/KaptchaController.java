package com.definesys.angrypecker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

//import com.definesys.dragon.service.KaptchaService;
//import com.google.code.kaptcha.impl.DefaultKaptcha;

@RestController
//@RequestMapping("/api")
public class KaptchaController {

//    @Autowired
//    private DefaultKaptcha kaptcha;
//    @Autowired
//    private KaptchaService service;

    @Autowired
    private ObjectMapper objectMapper;

//    @GetMapping(value = "/captcha")
//    public Response captcha() {
//        String text = kaptcha.createText();
//        System.out.println("生成验证码:" + text);
//
//        ByteArrayOutputStream baos = null;
//        BufferedImage image = kaptcha.createImage(text);
//        baos = new ByteArrayOutputStream();
//        Map<String, String> map = null;
//
//        try {
//            ImageIO.write(image, "jpg", baos);
//            BASE64Encoder encoder = new BASE64Encoder();
//            map = service.createToken(text);
//            map.put("img", encoder.encode(baos.toByteArray()));
//        } catch (IOException e) {
//            throw new MpaasBusinessException("验证码生成错误");
//        }
//        return Response.ok().data(map);
//    }
//
//
//    /**
//     * @description 生成图片验证码
//     */
//    @RequestMapping(value = "/static/a", method = {RequestMethod.POST, RequestMethod.GET})
//    @ResponseBody
//    public Response verification() throws IOException {
//
//        // 生成文字验证码
//        String text = kaptcha.createText();
//        // 生成图片验证码
//        ByteArrayOutputStream outputStream = null;
//        BufferedImage image = kaptcha.createImage(text);
//
//        outputStream = new ByteArrayOutputStream();
//        try{
//            ImageIO.write(image, "jpg",outputStream);
//        }catch (Exception e){
//            return Response.error("生成验证码失败");
//        }
//
//        // 对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//
//        // 生成captcha的token
//        Map map = new HashMap();
////        map = service.createToken(text);
//        map.put("img", "data:image/jpeg;base64,"+encoder.encode(outputStream.toByteArray()).replace("\r\n",""));
//        Date expirationDate = new Date(System.currentTimeMillis() + 300000);
//        String data = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).format(expirationDate);
//        String compact = DesUtil.enctypt(data, DragonUserController.secrets);
//
//        map.put("token",compact+"#"+text);
//        return Response.ok().setData(map);
//    }


}
