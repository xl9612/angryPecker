package com.definesys.angrypecker.pojo;

import com.definesys.mpaas.query.annotation.*;
import com.definesys.mpaas.query.json.MpaasDateDeserializer;
import com.definesys.mpaas.query.json.MpaasDateSerializer;
import com.definesys.mpaas.query.model.MpaasBasePojo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: wang
 * @since: 2018-11-02
 * @history: 1.2018-11-02 created by wang
 */

@Table(value = "fnd_users")
public class DragonUser extends MpaasBasePojo implements UserDetails {

    @RowID(sequence = "fnd_users_s", type = RowIDType.AUTO)
    private Integer id;

    //查看任务时使用，用于接收用户id（创建人、审批人）
    @Column(type=ColumnType.JAVA)
    private Integer userId;

    @Column(value = "login_email")
    private String loginEmail;

    @Column(value = "user_name")
    private String userName;

    private String password;

    private String salt;

    private String phone;

    private String status;

    //公司
    @Column(value = "company_id")
    private String companyId;

    /**
     * 图片验证码过期时间/
     */
    @Column(type = ColumnType.JAVA)
    private String token;

    /**
     * 图片验证码
     */
    @Column(type = ColumnType.JAVA)
    private String kaptcha;

    //is_authentication,是否认证,TRUE认证,FALSE未认证
    @Column(value = "is_authentication")
    private String isAuthentication;

    //职位
    private String position;

    //头像
    @Column(value = "user_icon")
    private String userIcon;

    @Column(type = ColumnType.JAVA)
    private Set<? extends GrantedAuthority> authorities;

    @SystemColumn(SystemColumnType.OBJECT_VERSION)
    @Column(value = "object_version_number")
    private Integer objectVersionNumber;

    @SystemColumn(SystemColumnType.CREATE_BY)
    @Column(value = "created_by")
    private String createdBy;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @SystemColumn(SystemColumnType.CREATE_ON)
    @Column(value = "creation_date")
    private Date creationDate;

    @SystemColumn(SystemColumnType.LASTUPDATE_BY)
    @Column(value = "last_updated_by")
    private String lastUpdatedBy;

    @JsonSerialize(using = MpaasDateSerializer.class)
    @JsonDeserialize(using = MpaasDateDeserializer.class)
    @SystemColumn(SystemColumnType.LASTUPDATE_ON)
    @Column(value = "last_update_date")
    private Date lastUpdateDate;

/*    object_version_number:数据版本号,从1开始计数,每次update操作都要把版本号+1
    created_by:创建用户的唯一标识符
    creation_date:创建时间
    last_updated_by:最后更新人的唯一标识符
    last_update_date:最后更新时间*/

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }
    public String getLoginEmail() {
        return this.loginEmail;
    }

    public void setLoginEmail(String loginEmail){
        this.loginEmail = loginEmail;
    }
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    //@JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    public void setAuthorities(Set<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password){
        this.password = password;
    }
    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position){
        this.position = position;
    }
    public String getUserIcon() {
        return this.userIcon;
    }

    public void setUserIcon(String userIcon){
        this.userIcon = userIcon;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getIsAuthentication() {
        return isAuthentication;
    }

    public void setIsAuthentication(String isAuthentication) {
        this.isAuthentication = isAuthentication;
    }

    public Integer getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Integer objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKaptcha() {
        return kaptcha;
    }

    public void setKaptcha(String kaptcha) {
        this.kaptcha = kaptcha;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DragonUser{" +
                "id=" + id +
                ", loginEmail='" + loginEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", phone='" + phone + '\'' +
                ", companyId='" + companyId + '\'' +
                ", isAuthentication='" + isAuthentication + '\'' +
                ", position='" + position + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", authorities=" + authorities +
                ", objectVersionNumber=" + objectVersionNumber +
                ", createdBy='" + createdBy + '\'' +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }
}