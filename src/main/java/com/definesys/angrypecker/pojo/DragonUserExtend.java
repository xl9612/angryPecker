package com.definesys.angrypecker.pojo;

import java.util.List;

public class DragonUserExtend extends DragonUser {

    //要修改的新密码
    private String newPassword;
    //具体什么操作
    private String specificOperation;

    private List roleName;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getSpecificOperation() {
        return specificOperation;
    }

    public void setSpecificOperation(String specificOperation) {
        this.specificOperation = specificOperation;
    }

    public List getRoleName() {
        return roleName;
    }

    public void setRoleName(List roleName) {
        this.roleName = roleName;
    }
}
