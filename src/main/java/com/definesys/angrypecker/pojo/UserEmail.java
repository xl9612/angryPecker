package com.definesys.angrypecker.pojo;

import com.definesys.mpaas.query.annotation.*;
import com.definesys.mpaas.query.model.MpaasBasePojo;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: wang
 * @since: 2018-11-22
 * @history: 1.2018-11-22 created by wang
 */
@Table(value = "dragon_user_email")
public class UserEmail extends MpaasBasePojo {

    private Integer id;

    private Long overtime;

    private String accounts;

    private String type;


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOvertime() {
        return this.overtime;
    }

    public void setOvertime(Long overtime) {
        this.overtime = overtime;
    }

    public String getAccounts() {
        return this.accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}