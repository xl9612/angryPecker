package com.definesys.angrypecker.pojo;

import com.definesys.mpaas.query.model.MpaasBasePojo;

/**
 * 时间条件
 */
public class TimeCondition extends MpaasBasePojo {

    private String timeOperation;//时间操作,今天,昨天,最近三天,本周,上周,本月

    private String startDate;//开始时间

    private String endDate;//结束时间

    public String getTimeOperation() {
        return timeOperation;
    }

    public void setTimeOperation(String timeOperation) {
        this.timeOperation = timeOperation;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate  = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
