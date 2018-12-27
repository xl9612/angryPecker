package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonRoleMember;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.pojo.FndJwtToken;
import com.definesys.angrypecker.properties.DragonConstants;
import com.definesys.angrypecker.service.DragonUserService;
import com.definesys.angrypecker.util.common.CURDUtils;
import com.definesys.angrypecker.util.common.DesUtil;
import com.definesys.angrypecker.util.common.DragonJwtTokenUtils;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQuery;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: wang
 * @since: 2018-11-05
 * @history: 1.2018-11-05 created by wang
 */
@RestController
@RequestMapping(value = "/api/user")
@EnableAsync
public class DragonUserController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @Autowired
    private DragonUserService dragonUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DragonJwtTokenUtils jwtTokenUtils;

    public static String secrets = "define_forget";

    /**
     * 登录
     * @param map
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response login(@RequestBody Map map) {
        String loginEmail = (String) map.get("loginEmail");
        String password = (String) map.get("password");

        if (ValidateUtils.checkIsNull(loginEmail))
            return Response.ok().data("邮箱不能为空");
        if (ValidateUtils.checkIsNull(password))
            return Response.ok().data("密码不能为空");

        DragonUser dragonUser = (DragonUser) validateLoginEmail(loginEmail).getData();
        if (dragonUser == null) {
            //可以弹出一个框,判断要不要发邮件
            return Response.ok().setMessage(loginEmail + "还未注册,请先去注册");
        }
        //当前密码加密,与原始密码比较
//        String currPassword = ShiroKit.md5(password, dragonUser.getSalt());
//        if (!currPassword.equals(dragonUser.getPassword())) {
        if (!passwordEncoder.matches(password,dragonUser.getPassword())) {
            return Response.ok().setMessage("密码不正确");
        }
        //生成JWT,默认一天时间
        String token = jwtTokenUtils.generateToken(dragonUser,86400L);
        FndJwtToken fndJwtToken = new FndJwtToken();
        fndJwtToken.setKey(token);
        fndJwtToken.setJwtToken(token);
        fndJwtToken.setOverdueDate(new Date());
        sw.buildQuery().doInsert(fndJwtToken);
        return Response.ok().data(dragonUser).setMessage(token);
    }

    public Response logout(){
        return Response.ok();
    }

    /**
     * 注册
     * @param item
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Response register(@RequestBody DragonUser item) {

        if (!ValidateUtils.checkIsNull(item.getToken()) && !ValidateUtils.checkIsNull(item.getKaptcha())){
            List<String> strings = Arrays.asList(item.getToken().split("#"));
            String s = strings.get(0);
            String s1 = strings.get(1);

            if (isExpired(s)){
                return Response.error("验证码以过期,请重新获取");
            }

            if (!item.getKaptcha().equalsIgnoreCase(s1)){
                return Response.error("图形验证码不正确");
            }
        }else if (ValidateUtils.checkIsNull(item.getKaptcha())){
            return Response.error("图形验证码不能为空");
        }else if (ValidateUtils.checkIsNull(item.getToken())){
            return Response.error("图形验证码的Token不能空");
        }

        if (ValidateUtils.checkIsNull(item.getPassword())) {
            return Response.error("密码不能为空");
        }
        if(ValidateUtils.checkIsNull(item.getLoginEmail()))
            return Response.error("邮箱不能为空");
        //验证邮箱输入是否为空，或邮箱是否已经被注册。
        item.setIsAuthentication("FALSE");
        item.setPassword(passwordEncoder.encode(item.getPassword()));
        //默认设置为未认证
        item.setStatus("2");
        Response response = validateLoginEmail(item.getLoginEmail());
        Object userKey = null;
        if (response.getData() != null) {
            DragonUser user = (DragonUser) response.getData();
            if(!"3".equals(user.getStatus()))
                return Response.error("该邮箱已注册");
            sw.buildQuery()
                    .bind(item)
                    .update(new String[]{"user_name","password","status"})
                    .eq("login_email",item.getLoginEmail())
                    .doUpdate(item);
            Map<String, Object> userMap = sw.buildQuery()
                    .sql("select id from fnd_users")
                    .eq("login_email", item.getLoginEmail())
                    .doQueryFirst();
            userKey = userMap.get("id");
            DragonRoleMember dragonRoleMember = new DragonRoleMember();
            dragonRoleMember.setJoinDate(new Date());
            dragonRoleMember.setUserId(Integer.valueOf(userKey.toString()));
            sw.buildQuery()
                    .bind(dragonRoleMember)
                    .update(new String[]{"user_id","join_date"})
                    .eq("user_id",userKey)
                    .doUpdate(dragonRoleMember);
        } else{
            userKey = sw.buildQuery()
                    .bind(item)
                    .doInsert();
        }
        return Response.ok().setMessage("注册成功");
    }
    private Response validateLoginEmail(String loginEmail) {
        if (ValidateUtils.checkIsNull(loginEmail)) {
            return Response.error("邮箱不能为空");
        }
        DragonUser dragonUser = sw.buildQuery()
                .eq("login_email", loginEmail)
                .doQueryFirst(DragonUser.class);
        return Response.ok().data(dragonUser);
    }

    /**
     * 判断该邮箱是否已经注册
     * @param map
     * @return
     */
    @PostMapping(value = "/validateLoginEmail")
    public Response checkLoginEmail(@RequestBody Map<String,String>map) {
        if(map==null)
            throw new MpaasBusinessException("邮箱不能为空");
        String loginEmail = map.get("loginEmail");
        if (ValidateUtils.checkIsNull(loginEmail)) {
            throw new MpaasBusinessException("邮箱不能为空");
        }
        DragonUser dragonUser = sw.buildQuery()
                .eq("login_email", loginEmail)
                .ne("status","3")
                .doQueryFirst(DragonUser.class);
        if(dragonUser!=null)
            throw new MpaasBusinessException("该邮箱已注册");
        return Response.ok();
    }


    /**
     * 校验重置密码邮箱里的超时
     * @param map
     * @return
     */
    @RequestMapping(value = "/validationexpired", method = RequestMethod.POST)
    public Response validationexpired(@RequestBody Map map) {
        String dateTime = (String)map.get("dateTime");
        if (isExpired(dateTime)){
            return Response.error("连接已失效,请重新获取");
        }
        return  Response.ok();
    }

    /**
     * 认证接口处理
     * @param map
     * @return
     */
    @RequestMapping(value = "/validationauthentication", method = RequestMethod.POST)
    public Response validationauthentication(@RequestBody Map map) {
        String dateTime = (String)map.get("dateTime");
        String loginEmail = (String)map.get("loginEmail");

        if (isExpired(dateTime)){
            return Response.error("连接已失效,请重新获取");
        }
        Response response = validateLoginEmail(loginEmail);
        DragonUser user = (DragonUser)response.getData();
        if (user == null){
            return  Response.error("该邮箱不存在");
        }
        user.setIsAuthentication("TRUE");
        sw.buildQuery()
                .eq("login_email",loginEmail)
                .doUpdate(user);
        return  Response.ok().setMessage("认证成功");
    }


    public boolean isExpired(String dateTime){
        if (ValidateUtils.checkIsNull(dateTime)){
            return true;
        }
        try {
            logger.info(dateTime);
            dateTime = DesUtil.decrypt(dateTime,secrets);
            Date parse = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).parse(dateTime);
            logger.info(dateTime+""+parse);
            if (dateTime != null && System.currentTimeMillis() - parse.getTime() < 0){
                return false;
            }
        }catch (Exception e){

        }
        return true;
    }

    /**
     * 重置密码
     * @param map
     * @return
     */
    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
    public Response updateUser(@RequestBody Map map) {
        String loginEmail = (String) map.get("loginEmail");
        String password = (String) map.get("password");
        if (ValidateUtils.checkIsNull(loginEmail))
            return Response.ok().setMessage("loginEmail不能为空");
        if (ValidateUtils.checkIsNull(password))
            return Response.ok().setMessage("密码不能为空");
        DragonUser updateUser = sw.buildQuery()
                .eq("login_email",  loginEmail)
                .doQueryFirst(DragonUser.class);
        if (updateUser == null)
            return Response.error("该邮箱不存在");
        updateUser.setPassword(passwordEncoder.encode(password));
        //部分字段更新
        sw.buildQuery()
                .addRowIdClause("id", "=", updateUser.getRowId())
                .doUpdate(updateUser)
                ;
        return Response.ok().setMessage("密码成功");
    }

    /**
     * 导出excel
     * @param response
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) {
        sw.buildQuery()
                .fileName("")
                .doExport(response, DragonUser.class);
    }

    /**
     * 重制密码时，验证邮箱是否注册，如果已注册，发送“重置密码”邮件，并向前端返回成功
     * 未注册则直接返回“邮箱未注册”提示信息。
     * @param map 待验证的邮箱
     * @return
     */
    @RequestMapping(value = "/forget/email", method = RequestMethod.POST)
    public Response forgetPassWord(@RequestBody Map<String, String> map) {
        String loginEmail = map.get("email");

        if (ValidateUtils.checkIsNull(loginEmail)){
            return Response.error("邮箱不能为空");
        }

        DragonUser dragonUser = sw.buildQuery()
                .sql("select * from fnd_users")
                .addClause("login_email", "=", loginEmail)
                .doQueryFirst(DragonUser.class);
        if (dragonUser == null) {
            throw new MpaasBusinessException("此邮箱还未注册，请重新输入邮箱!");
        }
        Date expirationDate = new Date(System.currentTimeMillis() + 7200000);
        String data = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).format(expirationDate);
        String compact = DesUtil.enctypt(data, secrets);
        logger.info("忘记密码->超时时间加密:"+compact);
        dragonUserService.sendResetPwdEmail(loginEmail, compact);
        return Response.ok().setMessage("邮件发送成功");
    }

    /**
     * 认证邮箱
     * @return
     */
    @PostMapping(value = "/certificateMail")
    public Response certificateMail(@RequestBody Map map){
        String returnUrl = (String)map.get("returnUrl");
        DragonUser principal = (DragonUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal.getLoginEmail();
        Date expirationDate = new Date(System.currentTimeMillis() + 7200000);
        String data = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).format(expirationDate);
        String compact = DesUtil.enctypt(data, secrets);
        //dragonUserService.sendCertificationEmail(email,compact);
        return Response.ok().setCode(compact)
                .setMessage("您的邮箱账户验证的邮箱已发送成功，请在两小时内进行验证。")
                .setData(returnUrl);
    }
    /**
     * 修改邮箱
     *
     * @param dragonUser 新邮箱、当前用户密码、rowId
     * @return 如果密码输入错误，返回提示“密码错误”否则修改邮箱，返回提示“修改邮箱成功”
     * @author xulei
     */
    @PostMapping(value = "/modify/mail")
    public Response modifyMail(@RequestBody DragonUser dragonUser) {
        if (ValidateUtils.checkIsNull(dragonUser.getLoginEmail()))
            new MpaasBusinessException("邮箱不能为空");
        if (ValidateUtils.checkIsNull(dragonUser.getPassword()))
            new MpaasBusinessException("密码不能为空");
        MpaasQuery query = sw.buildQuery();
        //获取指定rowId的dragonUser对象
        DragonUser user = query.sql("select * from fnd_users")
                .addRowIdClause("id", "=", dragonUser.getRowId())
                .doQueryFirst(DragonUser.class);
        boolean isPwdRight = passwordEncoder.matches(dragonUser.getPassword(),user.getPassword());
        if (user == null)
            throw new MpaasBusinessException("该用户不存在");
        if (!isPwdRight)
            throw new MpaasBusinessException("原密码错误");
        //修改邮箱
        CURDUtils.isLoginMailExist(query,dragonUser);
        //CURDUtils.update(query, dragonUser, dragonUser.getRowId(), "login_email");
        Date expirationDate = new Date(System.currentTimeMillis() + 7200000);
        String data = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).format(expirationDate);
        String compact = DesUtil.enctypt(data, secrets);
        dragonUserService.sendModifyEmail(dragonUser.getRowId(),dragonUser.getLoginEmail(),compact);
        return Response.ok().setMessage("邮件已发出,请及时完成修改");
    }
    /**
     * 修改邮箱——验证邮箱
     * @param map
     * @return
     */
    @PostMapping(value = "/modify/mail/auth")
    public Response modifyMailAuthentication(@RequestBody Map map){
        String dateTime = (String)map.get("dateTime");
        if (isExpired(dateTime)){
            throw new MpaasBusinessException("连接已失效,请重新获取");
        }
        String rowId = (String)map.get("rowId");
        String newEmail = (String)map.get("newEmail");
        if(ValidateUtils.checkIsNull(newEmail))
            return Response.error("邮箱不能为空");
        DragonUser dragonUser = new DragonUser();
        dragonUser.setRowId(rowId);
        dragonUser.setLoginEmail(newEmail);
        sw.buildQuery()
                .bind(dragonUser)
                .update(new String[]{"login_email"})
                .doUpdate(dragonUser);
        return Response.ok().setMessage("邮箱修改成功");
    }

    /**
     * 邀请好友注册
     * @param map
     * @return
     */
    @PostMapping(value = "/invite")
    public Response inviteFriends(@RequestBody Map<String,String>map){
        //获取前端传过来的邀请邮件
        String inviteMail = map.get("inviteMail");
        //邀请邮件判空处理
        if(ValidateUtils.checkIsNull(inviteMail))
            throw new MpaasBusinessException("邮箱不能为空");
        //获取前端传过来的项目角色id
        String roleId = map.get("roleId");
        //项目角色id判空处理
        if(ValidateUtils.checkIsNull(roleId))
            throw new MpaasBusinessException("角色id为空");
        //新建一个用户对象，并赋值。
        DragonUser newUser = new DragonUser();
        newUser.setLoginEmail(inviteMail);
        newUser.setUserName("受邀请的用户");
        newUser.setPassword(passwordEncoder.encode("welcome"));
        //设置该用户为受邀请状态
        newUser.setStatus("3");
        //设置该用户为未认证的状态
        newUser.setIsAuthentication("FALSE");
        //将该用户插入数据库
        Object newUserId = sw.buildQuery()
                .bind(newUser)
                .doInsert(newUser);
        //新建一个项目角色成员对象
        DragonRoleMember dragonRoleMember = new DragonRoleMember();
        //设置项目角色id
        dragonRoleMember.setRoleId(Integer.valueOf(roleId));
        //设置用户id
        dragonRoleMember.setUserId(Integer.valueOf(newUserId.toString()));
        //插入数据库
        Object roleNumberId = sw.buildQuery()
                .bind(dragonRoleMember)
                .doInsert(dragonRoleMember);
        //发送邀请邮件
        DragonUser dragonUser = dragonUserService.getDragonUser();
        Map<String, Object> stringObjectMap = sw.buildQuery()
                .sql("select project_name from dragon_projects")
                .addRowIdClause("id", "=", map.get("rowId"))
                .doQueryFirst();
        if(stringObjectMap==null)
            throw new MpaasBusinessException("该项目失效");
        Object project_name = stringObjectMap.get("project_name");
        Date expirationDate = new Date(System.currentTimeMillis() + 172800000);
        String data = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).format(expirationDate);
        String compact = DesUtil.enctypt(data, secrets);
        dragonUserService.sendInviteEmail(inviteMail,dragonUser.getUserName(),dragonUser.getLoginEmail(),project_name.toString(),compact);
        return Response.ok().setMessage("邀请已发出");
    }

    /**
     * 修改用户名
     *
     * @param dragonUser rowId、newName
     * @return 用户名为空返回提示“用户名为空”，否则返回提示”修改用户名成功！“
     * @author xulei
     */
    @PostMapping(value = "/modify/name")
    public Response modifyName(@RequestBody DragonUser dragonUser) {
        MpaasQuery query = sw.buildQuery();
        if (ValidateUtils.checkIsNull(dragonUser.getUserName()))
            return Response.error("用户姓名不能为空");
        //修改用户名
        CURDUtils.update(query, dragonUser, dragonUser.getRowId(), "user_name");
        return Response.ok().setMessage("修改用户姓名成功！");
    }

    /**
     * 修改密码
     * @param map rowId 用户标识 oldPwd 旧密码 newPwd 新密码
     * @return 旧密码验证错误，返回提示”原密码错误！“，否则返回提示”修改密码成功“。
     */
    @PostMapping(value = "/updateUserPassword")
    public Response modifyPassword(@RequestBody Map<String, String> map) {
        DragonUser principal = (DragonUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info(principal.getRowId()+"[]信息:"+principal);
        String rowId = principal.getRowId();
        String oldPwd = map.get("password");
        String newPwd = map.get("newPassword");
        if (ValidateUtils.checkIsNull(oldPwd))
            return Response.error("原密码不能为空");
        if (ValidateUtils.checkIsNull(newPwd))
            return Response.error("新密码不能为空");

        //判断密码是否正确
        DragonUser dragonUser = sw.buildQuery()
                                    .addRowIdClause("id","=",rowId)
                                    .doQueryFirst(DragonUser.class);

        if (dragonUser != null && !passwordEncoder.matches(oldPwd,dragonUser.getPassword())){
            return Response.error("原密码错误");
        }
        //对新密码加密
        dragonUser.setPassword(passwordEncoder.encode(newPwd));
        //执行更新密码操作
        sw.buildQuery().addRowIdClause("id","=",rowId).doUpdate(dragonUser);
        return Response.ok().setMessage("修改密码成功！");
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout")
    public Response logoutSuccess(HttpServletRequest request){
        String token = (String)request.getAttribute("token");
        sw.buildQuery().bind(FndJwtToken.class).eq("jwt_key",token).doDelete();
        logger.info("退出登录");
        return Response.ok().setMessage("退出成功");
    }

}