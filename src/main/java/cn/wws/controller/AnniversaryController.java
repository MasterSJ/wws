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

import com.alibaba.fastjson.JSONObject;

import cn.wws.controller.base.BaseController;
import cn.wws.entity.ReturnResult;
import cn.wws.entity.User;
import cn.wws.service.AnniversaryService;
import cn.wws.util.RequestUtil;

@Controller 
public class AnniversaryController extends BaseController{
    @Autowired
    AnniversaryService anniversaryService;
    
    /**纪念日详情*/
    @RequestMapping("/showAnniversaryRemind")  
    public String showAnniversaryRemind(HttpServletRequest request, Model model) {
        User user = RequestUtil.getRequestUser();
        List<Map<String, String>> list = anniversaryService.getAnniversaryByUserName(user.getUserName());
        model.addAttribute("anniversaries", list);
        return "anniversary/userAnniversariesInfo"; 
    }
    
    /**纪念日详情*/
    @RequestMapping("/editAnniversaryRemind")  
    public String editAnniversaryRemind(HttpServletRequest request,
            @RequestParam(required=false) String id, Model model) {
        if (StringUtils.isEmpty(id)) {
            return "anniversary/anniversaryEdit";
        }
        Map<String, String> map = anniversaryService.getAnniversaryById(id);
        model.addAllAttributes(map);
        return "anniversary/anniversaryEdit"; 
    }
    
    @RequestMapping("/anniversaryRemind/doEdit")  
    @ResponseBody
    public ReturnResult doEdit(HttpServletRequest request,
            @RequestBody Map<String, String> param) {
        ReturnResult rst = new ReturnResult();
        if (param == null || param.isEmpty()) {
            rst.setFailMsg("参数为空！");
            return rst;
        }
        User user = RequestUtil.getRequestUser();
        param.put("userName", user.getUserName());
        if (StringUtils.isEmpty(param.get("id"))) {
            anniversaryService.insertAnniversary(param);
        } else {
            anniversaryService.updateAnniversary(param);
        }
        rst.setSuccess();
        return rst; 
    }
}
