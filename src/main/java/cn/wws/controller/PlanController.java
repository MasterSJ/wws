package cn.wws.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.wws.entity.ReturnResult;
import cn.wws.entity.User;
import cn.wws.service.PlanService;
import cn.wws.util.DateTimeUtil;
import cn.wws.util.RequestUtil;

@Controller 
public class PlanController {
    @Autowired
    PlanService planService;
    
    /**我的计划*/
    @RequestMapping("/showPlanManage")  
    public String showPlanManage(HttpServletRequest request, Model model) {
        User user = RequestUtil.getRequestUser();
        
        List<Map<String, Object>> list = planService.getPlanManageByUserName(user.getUserName());
        model.addAttribute("planManage", list);
        return "user/planManage"; 
    }
    
    @RequestMapping("/addPlanManage")  
    public String addPlanManage(HttpServletRequest request, Model model) {
        return "user/planManageEdit"; 
    }
    
    /**编辑计划*/
    @RequestMapping("/editPlanManage")  
    public String editAnniversaryRemind(HttpServletRequest request,
            @RequestParam(required=false) String id, Model model) {
        if (!StringUtils.isEmpty(id)) {
            User user = RequestUtil.getRequestUser();
            Map<String, Object> map = planService.getPlanManageById(user.getUserName(), id);
            model.addAllAttributes(map);
        }
        return "user/planManageEdit"; 
    }
    
    /**删除计划*/
    @RequestMapping("delPlanManage")
    @ResponseBody
    public ReturnResult delPlanManage(HttpServletRequest request,
            @RequestParam int id) {
        ReturnResult rst = new ReturnResult();
        if (id <= 0) {
            rst.setFailMsg("参数异常！");
            return rst;
        }
        User user = RequestUtil.getRequestUser();
        int count = planService.deletePlanManage(id, user.getUserName());
        if(count > 0){
            rst.setSuccess();
        }
        return rst; 
    }
    
    @RequestMapping("/planManage/doEdit")  
    @ResponseBody
    public ReturnResult doEdit(HttpServletRequest request,
            @RequestBody Map<String, Object> param) {
        ReturnResult rst = new ReturnResult();
        if (param == null || param.isEmpty()) {
            rst.setFailMsg("参数为空！");
            return rst;
        }
        User user = RequestUtil.getRequestUser();
        param.put("userName", user.getUserName());
        String id = param.get("id").toString();
        if (StringUtils.isEmpty(id)) {
            String lastRemindDate = (String) param.get("lastRemindDate");
            if(StringUtils.isEmpty(lastRemindDate)){
                lastRemindDate = DateTimeUtil.getCurrentDate();
                param.put("lastRemindDate", lastRemindDate);
            }
            int update = planService.insertPlanManage(param);
            if(update > 0){
                planService.insertUserPlanRemind(user.getUserName());
            }
        } else {
            planService.updatePlanManage(param);
        }
        rst.setSuccess();
        return rst; 
    }
    
    /**我的计划提醒*/
    @RequestMapping("/showPlanRemind")  
    public String showPlanRemind(HttpServletRequest request, Model model) {
        User user = RequestUtil.getRequestUser();
        
        List<Map<String, Object>> list = planService.getUserPlanRemind(user.getUserName());
        model.addAttribute("planRemind", list);
        return "user/planRemind"; 
    }
    
    /**完成一次任务*/
    @RequestMapping("/finishPlanRemind")
    @ResponseBody
    public ReturnResult finishPlanRemind(HttpServletRequest request,
            @RequestParam int id) {
        ReturnResult rst = new ReturnResult();
        if (id <= 0) {
            rst.setFailMsg("参数异常！");
            return rst;
        }
        int count = planService.finishPlanRemind(id);
        if(count > 0){
            rst.setSuccess();
        }
        return rst; 
    }
}
