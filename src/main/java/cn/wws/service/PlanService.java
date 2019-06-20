package cn.wws.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.wws.entity.PlanTypeEnum;
import cn.wws.util.DateTimeUtil;

@Service
public class PlanService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PlanService.class);
    @Autowired
    BaseService baseService;
    
    public List<Map<String, Object>> getPlanManageByUserName(String userName) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userName", userName);
        List<Map<String, Object>> list = baseService.executeQuery(
                "plan.getPlanManageByUserName", param);
        return list;
    }
    
    public int insertPlanManage(Map<String, Object> paramMap){
        int insert = baseService.executeInsert("plan.insertPlanManage", paramMap);
        return insert;
    }
    
    public int insertPlanRemind(int planManageId, String remindDate){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("planManageId", planManageId);
        param.put("remindDate", remindDate);
        int insert = baseService.executeInsert("plan.insertPlanRemind", param);
        return insert;
    }
    
    public int updatePlanManage(Map<String, Object> paramMap){
        int insert = baseService.executeInsert("plan.updatePlanManage", paramMap);
        return insert;
    }
    
    public Map<String, Object> getPlanManageById(String userName, String id) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userName", userName);
        param.put("id", id);
        List<Map<String, Object>> list = baseService.executeQuery(
                "plan.getPlanManageById", param);
        if(list != null){
            return list.get(0);
        }
        return null;
    }
    
    public int insertUserPlanRemind(String userName){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userName", userName);
        List<Map<String, Object>> list = baseService.executeQuery("plan.getUnremindPlanManageByUserName", param);
        if(list == null || list.isEmpty()){
            return 0;
        }
        for(Map<String, Object> plan : list){
            param.put("planManageId", plan.get("id"));
            param.put("remindDate", plan.get("last_remind_date"));
            baseService.executeInsert("plan.insertPlanRemind", param);
        }
        return list.size();
    }
    
    public int deletePlanManage(int planId, String userName){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("planId", planId);
        param.put("userName", userName);
        int count = baseService.executeDelete("plan.deletePlanManage", param);
        if(count > 0){
            return baseService.executeDelete("plan.deletePlanRemindByPlanId", param);
        }
        return 0;
    }
    
    //凌晨4点生成第二天的计划提醒
    @Scheduled(cron = "0 27 16 * * ?")
    public void generatePlanRemind() {
        Calendar cal = Calendar.getInstance();
        //明天的日期
        cal.add(Calendar.DATE, 1);
        Date monday = cal.getTime();
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
        String tomorrow = sdfd.format(monday);
        if(cal.get(Calendar.DAY_OF_WEEK) == 2){
            //明天是周一，生成下周的周计划
            List<Map<String, Object>> weekRemind = getPlanManageByDateAndType(tomorrow, PlanTypeEnum.WEEK.getCode());
            generatePlanRemind(weekRemind, tomorrow);
        }
        if(cal.get(Calendar.DAY_OF_MONTH) == 1){
            //明天是1号，生成下月的月度计划
            List<Map<String, Object>> monthRemind = getPlanManageByDateAndType(tomorrow, PlanTypeEnum.MONTH.getCode());
            generatePlanRemind(monthRemind, tomorrow);
        }
        if(cal.get(Calendar.DAY_OF_YEAR) == 1){
            //明天是元旦，生成明年的年度计划
            List<Map<String, Object>> yearRemind = getPlanManageByDateAndType(tomorrow, PlanTypeEnum.YEAR.getCode());
            generatePlanRemind(yearRemind, tomorrow);
        }
        //生成明天的每日计划
        List<Map<String, Object>> datRemind = getPlanManageByDateAndType(tomorrow, PlanTypeEnum.DAY.getCode());
        generatePlanRemind(datRemind, tomorrow);
    }
    
    public List<Map<String, Object>> getPlanManageByDateAndType(String date, String planType) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("date", date);
        param.put("planType", planType);
        List<Map<String, Object>> list = baseService.executeQuery("plan.getPlanManageByDateAndType", param);
        return list;
    }
    
    public void generatePlanRemind(List<Map<String, Object>> planIds, String date){
        if(planIds != null){
            for(Map<String, Object> idMap : planIds){
                idMap.put("remindDate", date);
                idMap.put("planManageId", idMap.get("id"));
                baseService.executeInsert("plan.insertPlanRemind", idMap);
            }
        }
    }
    
    public List<Map<String, Object>> getUserPlanRemind(String userName) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userName", userName);
        Date today = new Date();
        String firstOfWeek = DateTimeUtil.getThisWeekMonday(today);
        param.put("weekStart", firstOfWeek);
        String firstOfMonth = DateTimeUtil.getFirstDayOfMonth(today);
        param.put("monthStart", firstOfMonth);
        String firstOfYear = DateTimeUtil.getFirstDayOfYear(today);
        param.put("yearStart", firstOfYear);
        List<Map<String, Object>> list = baseService.executeQuery("plan.getUserPlanRemind", param);
        return list;
    }
    
    public int finishPlanRemind(int remindId){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("planRemindId", remindId);
        List<Map<String, Integer>> status = baseService.executeQuery("plan.getRemindFinishStatus", param);
        if(status != null && !status.isEmpty()){
            Map<String, Integer> rst = status.get(0);
            int planTimes = rst.get("plan_times");
            int finishedTimes = rst.get("finished_times");
            if(planTimes - finishedTimes <= 1){
                param.put("finished", "Y");
            }
        }
        
        int update = baseService.executeUpdate("plan.finishPlanRemind", param);
        return update;
    }
    
    public static void main(String[] args){
        PlanService s = new PlanService();
        s.generatePlanRemind();
    }
}
