package controller;


import common.JsonData;
import dao.sys.SysAclModuleMapper;
import exception.ParamException;
import exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import model.sys.SysAclModule;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import param.TestVo;
import util.ApplicationContextHelper;
import util.BeanValidator;
import util.JsonMapper;


@Controller
@RequestMapping("test")
@Slf4j
public class TestController {

    @RequestMapping("hello.json")
    @ResponseBody
    public JsonData hello(){
        log.info("hello");
        throw new PermissionException("test exception");
//        return JsonData.success("hello ,permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo testVo) throws ParamException{
        log.info("validate");

        SysAclModuleMapper moduleMapper= ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule sysAclModule=moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(sysAclModule));
        BeanValidator.check(testVo);
        return JsonData.success("test validate");
    }
}
