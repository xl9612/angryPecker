package com.definesys.angrypecker.pojo;

import com.definesys.mpaas.query.annotation.*;
import com.definesys.mpaas.query.json.MpaasDateDeserializer;
import com.definesys.mpaas.query.json.MpaasDateSerializer;
import com.definesys.mpaas.query.model.MpaasBasePojo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: 阳
 * @since: 2018-11-23
 * @history: 1.2018-11-23 created by 阳
 */
@SQLQuery(value = {
        @SQL(view = "projectmember_v", sql = "SELECT dpr.id as roleId,dpr.role_code as roleCode,(SELECT flv.meaning FROM fnd_lookup_values flv where flv.lookup_code = dpr.role_code AND flv.lookup_id = 6 ) roleName,drm.id as memberId,drm.user_id as userId, drm.join_date as joinDate, drm.quit_date as quitDate,fu.login_email as loginEmail,fu.user_name as userName,IF(fu.status = '3','邀请中',null) as status,fu.user_icon as userIcon,dp.id as project_id from dragon_project_role dpr  LEFT JOIN dragon_role_member drm ON dpr.id = drm.role_id  LEFT JOIN fnd_users fu ON fu.id = drm.user_id  LEFT JOIN dragon_projects dp ON dp.id = dpr.project_id  order by dpr.id"),
}
)
public class ProjectMember extends MpaasBasePojo {

    private Integer roleid;

    private String rolename;

    private Integer memberid;

    private String status;

    private String userIcon;

    private String roleCode;

    private Integer userId;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    private Date joindate;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    private Date quitdate;

    private String loginemail;

    private String username;

    @Column(value = "project_id")
    private Integer projectId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public Integer getRoleid() {
        return this.roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public String getRolename() {
        return this.rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public Integer getMemberid() {
        return this.memberid;
    }

    public void setMemberid(Integer memberid) {
        this.memberid = memberid;
    }

    public Date getJoindate() {
        return this.joindate;
    }

    public void setJoindate(Date joindate) {
        this.joindate = joindate;
    }

    public Date getQuitdate() {
        return this.quitdate;
    }

    public void setQuitdate(Date quitdate) {
        this.quitdate = quitdate;
    }

    public String getLoginemail() {
        return this.loginemail;
    }

    public void setLoginemail(String loginemail) {
        this.loginemail = loginemail;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
}