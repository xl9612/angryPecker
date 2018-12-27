package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.pojo.FndJwtToken;
import com.definesys.angrypecker.util.common.DragonJwtTokenUtils;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全认证接口
 */
//@RestController
@Controller
@RequestMapping("/api/dragon")
public class RestSecurityController {

    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private DragonJwtTokenUtils jwtTokenUtil;

    @RequestMapping(value = "/securityAuthentication",method = RequestMethod.POST)
    @ResponseBody
    public Map validateApiSecurlty(@RequestBody Map map) {
        String tokenHead = "Bearer ";
        String token = (String) map.get("token");
        map = new HashMap();
        if (!ValidateUtils.checkIsNull(token) && token.startsWith(tokenHead)){
            token = token.substring(tokenHead.length());
            FndJwtToken myToken = sw.buildQuery().eq("jwt_key", token).doQueryFirst(FndJwtToken.class);
            if (myToken != null && !ValidateUtils.checkIsNull(myToken.getJwtToken())) {
                String rowId = jwtTokenUtil.getUsernameFromToken(myToken.getJwtToken());
                if (rowId != null) {
                    DragonUser dragonUser = sw.buildQuery()
                            .addRowIdClause("id","=", rowId)
                            .doQueryFirst(DragonUser.class);
                    if (jwtTokenUtil.validateToken(token, dragonUser)) {
                        map.put("msg","AuthenticationSuccess");
                        return map;
                    }
                }
            }
        }

        map.put("msg","AuthenticationFail");
        return map;
    }

}
