package com.definesys.angrypecker.service;

import com.definesys.angrypecker.pojo.DragonProInfo;
import com.definesys.angrypecker.pojo.DragonProjects;
import com.definesys.angrypecker.pojo.DragonUserProjectInfo;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Chico Lee
 *
 */
@Service
public class DragonProjectLine {

    @Autowired
    private MpaasQueryFactory sw;

    public List<DragonProInfo> getProjectLineMsg (List<DragonProInfo> projectInfos, Integer userId){
        for (DragonProInfo projectInfo : projectInfos) {
//            Map<String, Object> count = sw.buildQuery()
//                    .sql("SELECT COUNT(1) as counts FROM dragon_tasks_info dti\n" +
//                            "LEFT JOIN dragon_user_task dut ON dti.user_task_id = dut.id\n" +
//                            "LEFT JOIN dragon_projects dp ON dti.project_id = dp.id")
//                    .addClause("user_id", "=", userId)
//                    .addClause("project_id", "=", projectInfo.getId())
//                    .doQueryFirst();
//            projectInfo.setGtasksNum(Integer.parseInt(count.get("counts").toString()));
            if (userId.equals(projectInfo.getProjectOwnerId())) {
                projectInfo.setOwner(TRUE);
                projectInfo.setManager(TRUE);
            } else {
                projectInfo.setOwner(FALSE);
            }

            if(!TRUE.equals(projectInfo.getManager())) {
                if(this.isManagerRoleForPro(projectInfo.getId(),userId,"T")){
                    projectInfo.setManager(TRUE);
                }else {
                    projectInfo.setManager(FALSE);
                }
            }

            Map<String,Object> sort = sw.buildQuery()
                    .sql("select t.id,t.sort,t.is_notification from dragon_user_project_info t")
                    .addClause("user_id","=",userId)
                    .addClause("project_id","=",projectInfo.getId())
                    .doQueryFirst();

            if(sort!=null){
                if("1".equals(sort.get("is_notification"))){
                    projectInfo.setSend(TRUE);
                }else {
                    projectInfo.setSend(FALSE);
                }
                projectInfo.setSortId((Integer)sort.get("id"));
                projectInfo.setSort((Integer)sort.get("sort"));
            }
        }

        projectInfos=this.getProListForSort(projectInfos);
        return projectInfos;
    }

    public boolean isManagerRoleForPro(Integer projectId,Integer userId,String unNeed){
        boolean status = FALSE;

        List<Map<String, Object>> queryResult = sw.buildQuery()
                .sql("SELECT dpr.role_code FROM dragon_project_role dpr\n" +
                        "LEFT JOIN dragon_role_member drm ON drm.role_id = dpr.id")
                .addClause("user_id","=",userId)
                .addClause("project_id","=",projectId)
                .doQuery();

        Map<String ,Object> map = new HashMap<>();
        map.put("role_code","PRO_DIRECTOR");
        Map<String ,Object> map1 = new HashMap<>();
        map1.put("role_code","PRO_MANAGER");

        if(queryResult.contains(map)){
            status = TRUE;
        }
        if(queryResult.contains(map1)){
            status = TRUE;
        }

        /**
         * 通过unNeed为"",判断是否需要为创建人
         */
        if("".equals(unNeed)) {
            DragonProjects projects = sw.buildQuery()
                    .sql("select * from dragon_projects")
                    .addClause("id", "=", projectId)
                    .doQueryFirst(DragonProjects.class);
            if (userId.equals(projects.getProjectOwnerId())) {
                status = TRUE;
            }
        }
        return status;
    }


    public List<DragonProInfo> getProListForSort (List<DragonProInfo> dragonProInfos){
        Comparator<DragonProInfo> comparator=new Comparator<DragonProInfo>() {
            public int compare(DragonProInfo o1, DragonProInfo o2) {

                if(o1 ==null && o2==null){
                    return 0;
                }
                if(o2 == null){
                    return 1;
                }
                if(o1 == null){
                    return -1;
                }
                if(o2.getSort()== null && o1.getSort() == null){
                    return 0;
                }
                if(o2.getSort() == null){
                    return 1;
                }
                if(o1.getSort() ==null){
                    return -1;
                }
                if(o2.getSort() > o1.getSort()) {
                    return -1;
                }
                if(o2.getSort() < o1.getSort()){
                    return 1;
                }
                return 0;
            }
        };
        Collections.sort(dragonProInfos, comparator);
        return dragonProInfos;
    }


    public Object insertProSort (Integer userId,Integer projectId){
        Object key = new Object();
        Map m = sw.buildQuery()
                .sql("SELECT sort  FROM `dragon_user_project_info`")
                .addClause("user_id","=",userId)
                .addClause("project_id","=",projectId)
                .doQueryFirst();

        if(m==null){
            DragonUserProjectInfo userProjectInfo = new DragonUserProjectInfo();
            userProjectInfo.setProjectId(projectId);
            userProjectInfo.setUserId(userId);
            Map s = sw.buildQuery()
                    .sql("SELECT MAX(sort) as sort FROM `dragon_user_project_info`")
                    .addClause("user_id","=",userId)
                    .doQueryFirst();

            if(s==null) {
                userProjectInfo.setSort(0);
            }else {
                userProjectInfo.setSort((Integer) s.get("sort") + 1);
            }
            key = sw.buildQuery()
                    .bind(userProjectInfo)
                    .doInsert();
        }
        return key;
    }


    public boolean isHaveRole (Integer projectId,Integer userId){

        List<Map<String,Object>> list = sw.buildQuery()
                .sql("SELECT dpr.role_code FROM dragon_project_role dpr\n" +
                        "LEFT JOIN dragon_role_member drm ON dpr.id = drm.role_id")
                .addClause("project_id","=",projectId)
                .addClause("user_id","=",userId)
                .doQuery();

        if(list.size()>0){
            return TRUE;
        }else {
            return FALSE;
        }

    }

    public boolean isHaveBasicRoleForPro(Integer projectId,String unNeed){
        List<Map<String, Object>> queryResult = sw.buildQuery()
                .sql("select distinct dpr.role_code from dragon_project_role dpr \n" +
                        "left join dragon_role_member drm on drm.role_id = dpr.id ")
                //.addClause("user_id","=",userId)
                .addClause("project_id","=",projectId)
                .groupBy("dpr.role_code having (count(drm.role_id) > 0)")
                .doQuery();

        Map<String ,Object> map = new HashMap<>();
        map.put("role_code","PRO_DIRECTOR");//项目总监
        Map<String ,Object> map1 = new HashMap<>();
        map1.put("role_code","PRO_MANAGER");//项目经理
        Map<String ,Object> map2 = new HashMap<>();
        map2.put("role_code","PRO_TECH_MANAGER");//技术负责人
        Map<String ,Object> map3 = new HashMap<>();
        map3.put("role_code","PRODUCT_MANAGER");//产品经理

        if(queryResult.contains(map) && queryResult.contains(map1)
                && queryResult.contains(map2) && queryResult.contains(map3)){

            return true;
        }


        return false;
    }

    /**
     * 获得这个项目下的所有(角色)成员
     * @param projectId
     * @return
     */
    public List<Map<String, Object>> getProjectAllRoleMemberById(Integer projectId){
        String sql = "SELECT DISTINCT drm.user_id as id FROM dragon_role_member drm WHERE drm.role_id IN ( SELECT dpr.id FROM dragon_project_role dpr WHERE dpr.project_id = "+projectId+" )";
        List<Map<String, Object>> dragonProInfo = sw.buildQuery()
                .sql(sql)
                .doQuery();

        return dragonProInfo;
    }

    /**
     * 判断当前登录人是否在这个项目下
     * @param loginId
     * @param projectId
     * @return
     */
    public boolean projectMemberIsExisteCurrLogin(Integer loginId,Integer projectId){
        List<Map<String, Object>> projectAllRoleMember = getProjectAllRoleMemberById(projectId);
        if (projectAllRoleMember == null || projectAllRoleMember.size() < 0){
            return false;
        }
        Map map = new HashMap();
        map.put("id",loginId);
        return projectAllRoleMember.contains(map);
    }
}
