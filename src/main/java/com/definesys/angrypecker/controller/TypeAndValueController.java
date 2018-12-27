package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.exception.DragonException;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.pojo.FndLookupTypes;
import com.definesys.angrypecker.pojo.FndLookupValues;
import com.definesys.angrypecker.properties.DragonConstants;
import com.definesys.angrypecker.util.common.DesUtil;
import com.definesys.angrypecker.util.support.SCaptcha;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.common.exception.MpaasRuntimeException;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.definesys.dragon.service.KaptchaService;
//import com.google.code.kaptcha.impl.DefaultKaptcha;

@RestController
@RequestMapping("/api/fnd")
public class TypeAndValueController {

//    @Autowired
//    private DefaultKaptcha kaptcha;
//    @Autowired
//    private KaptchaService service;

    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @RequestMapping("/getTypeAndValue")
    public Response getTypeAndValue(){
        String sql = "select t.id,t.lookup_type lookupType,t.lookup_name lookupName from fnd_lookup_types t";
        String sqlv = "select v.id,v.lookup_code lookupCode,meaning,description from fnd_lookup_values v";

        Map map = new HashMap();
        List<FndLookupValues> values = null;

        List<FndLookupTypes> types = sw.buildQuery()
                .sql(sql)
                .doQuery(FndLookupTypes.class);

        for (FndLookupTypes type : types){
            values = sw.buildQuery()
                    .sql(sqlv)
                    .eq("lookup_id", type.getId())
                    .eq("enabled_flag","TRUE")
                    .doQuery(FndLookupValues.class);

            map.put(type.getLookupType(),values);

        }

        return Response.ok().setData(map);
    }

    @RequestMapping(value = "/generateCheckCode", method = RequestMethod.GET)
    public Response captcha(HttpServletResponse response) throws ServletException {

        //实例生成验证码对象
        SCaptcha instance = new SCaptcha();
        //将验证码存入session
        Map map = new HashMap();

        BASE64Encoder encoder = new BASE64Encoder();
        try {
            ByteArrayOutputStream outputStream = instance.write(new ByteArrayOutputStream());
            map.put("img", "data:image/jpeg;base64,"+encoder.encode(outputStream.toByteArray()).replace("\r\n",""));
            Date expirationDate = new Date(System.currentTimeMillis() + 300000);
            String data = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).format(expirationDate);
            String compact = DesUtil.enctypt(data, DragonUserController.secrets);
            map.put("token",compact+"#"+instance.getCode());
            outputStream.close();

        }catch (Exception e){
            return Response.error("生成验证码错误,请联系管理员");
        }
        return Response.ok().setData(map);

    }

    @RequestMapping("/testException")
    public Response testException(@RequestBody Map map){
        String type = (String)map.get("type");
        if ("dragon".equals(type)){
            throw new DragonException("测试自定义DragonException");
        }else if ("run".equals(type)){
            throw new MpaasRuntimeException("测试倚天运行时DragonException");
        }else if ("bug".equals(type)){
            throw new MpaasBusinessException("测试倚天业务异常DragonException");
        }
        String sql = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a\n" +
                "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy \n" +
                "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAiAMgDASIA \n" +
                "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA \n" +
                "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3 \n" +
                "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\n" +
                "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA\n" +
                "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\n" +
                "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\n" +
                "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\n" +
                "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD0EU4U\n" +
                "0U4UAPFOFNFOFADhTxTRThQA4U8U0U4UAOFPFNFcnrHjG6sPECaVY6Ld30nlmR2jQhQoIHU8f3vx\n" +
                "x60AdgKcWCqWYgKBkk9AKxNA8TWGvwv5BeK5iIWa2mXbJExzwR/wE/lWT4x8QjTp47H7UkBmKoWa\n" +
                "UR/fJHBPU8HJ525UnGdygHWfbrUNtNxEG8zysbxnf/d+vI49xVpSGAI6GvnzV4/td9p0thOJr+5l\n" +
                "SFUjiHMZ+YOCc7d5J5cbmIOcYIr2htRsvC3h+GTU5UgjjQ52j5cqhYgDtwp/EgdSBQBvCnCuW0r4\n" +
                "g+GtYm8m11KMzb9gQ8EnciDHY5LqB+Poa3L+/a30W4vrSI3LLbtLEiDPmEKWUD64A/GgCxdX9nYI\n" +
                "HvLqG3UnAMrhQfz+tWIpEmjWSJ1dGGVZTkGvIJvAy+IYdQ8S+Nrq5t8nNvbySbFgTHGR2O49P8a2\n" +
                "PgpLeT+EZpJ2kNqJzHZiTr5Y7/ixb8qAPTBThTRTxQA4U4U0U4UAPFOFNFOFADhRSiigDhBTxTRT\n" +
                "hQA4U8U0U4UAOFPFNFOFADhTxTBTxQA7oM15lqGva/f+Lbyzh1mDSdPhaREZog5fYPmJz0GT+lem\n" +
                "isTWvB2i+IHWTULUPIucMpwRnHp9KAOF+HcV1H4/1hr6eOdtpjSdFws0gbczAeoV8Z9DVr4lrBqt\n" +
                "xDpVtHAZZQwmcSAPtyMhRnLMGVTgZ4TJ6V0ug+BbPw5qDXGn3EuxiG2zEOQcYbBI7jHPtVjWfDU1\n" +
                "5cyXFnIE80ESR7tmSRjIIH44PfnIoA8iuNCsNG1XTIdDu3url2kVnFzv3rgABk6847Aeme47f4kW\n" +
                "91NodlNfzRJblFS4kZCQwBV9uBwGZk69BXUaH4NstOZrq5Xz7yZV8x5NjsCMcBwikjgDHA46V0F7\n" +
                "YW2o2MtldRLJBKpVlPpQBx9z4O8J+I7CzlsjDZzJtkimttiSKMh9px1xkfSu2t44rCyji34iiQKG\n" +
                "YgYA4Fea2Xwlk0m/aTTdanS1JfbA7EgKwC4+uO/09K73UdDj1HQpNK+0zwq4UearZdcEHIzxnigD\n" +
                "yTXvFel+NPE0tlq2sxab4bsmKlN533bBh1UcgAivXvDV3o15ocEmgSRSacu5YzECFBB5HPvXPaZ8\n" +
                "J/B+nqWfS1vJmJLzXbFyx9cfd/IV1ml6VYaNZi0061itrcMWEcQwoJ68UAXhThTRThQA4U8U0U4U\n" +
                "AOFPFNFOFADhRSiigDhBTxRRQA4U8UUUAOFOFFFADxThRRQA8U4UUUAPFOFFFADxThRRQA4U8UUU\n" +
                "AOFPFFFADhTxRRQA4U8UUUAOFPFFFADhRRRQB//Z";
        DragonUser dragonUser = new DragonUser();
        dragonUser.setUserIcon(sql);
        Map map1 = new HashMap<>();

        map1.put("str",sql);
        map1.put("obj",dragonUser);
        map1.put("tt",sql.replace("\n",""));
        return Response.ok().setData(map1);
    }
}