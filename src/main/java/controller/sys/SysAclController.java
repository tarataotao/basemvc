package controller.sys;

import beans.PageQuery;
import beans.PageResult;
import common.JsonData;
import lombok.extern.slf4j.Slf4j;
import model.sys.SysAcl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import param.AclParam;
import service.sys.SysAclService;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {

    @Resource
    private SysAclService sysAclService;

    @RequestMapping("save.json")
    @ResponseBody
    public JsonData saveAclModule(AclParam param){
        sysAclService.save(param);
        return JsonData.success();
    }

    @RequestMapping("update.json")
    @ResponseBody
    public JsonData updateAclModule(AclParam param){
        sysAclService.update(param);
        return JsonData.success();
    }

    @RequestMapping("page.json")
    @ResponseBody
    public JsonData list(@RequestParam("aclModuleId")  Integer  aclModuleId, PageQuery pageQuery){
        PageResult<SysAcl> page=sysAclService.getPageByAclModuleId(aclModuleId,pageQuery);
        return JsonData.success(page);
    }
}
