package controller.sys;

import beans.PageQuery;
import beans.PageResult;
import common.JsonData;
import model.sys.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import param.UserParam;
import service.sys.SysUserService;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveSysUser(UserParam param){
        sysUserService.save(param);
        return JsonData.success();
    }
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateSysUser(UserParam param){
        sysUserService.update(param);
        return JsonData.success();
    }

    @RequestMapping("page.json")
    @ResponseBody
    public JsonData page(@RequestParam("deptId") int deptId, PageQuery pageQuery){
       PageResult<SysUser> list =sysUserService.getPageByDeptId(deptId,pageQuery);
       return JsonData.success(list);
    }

}
