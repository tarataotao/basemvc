package exception;

import common.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 陶杰
 * 全局异常处理逻辑
 */
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String url= httpServletRequest.getRequestURI().toString();
        ModelAndView mv;
//        String defaultMsg="System error";
        String defaultMsg=e.getMessage();
        //.json,.page
        //这里我们要求项目中所有请求json数据，都使用json结尾
        if(url.endsWith(".json")){
            if(e instanceof PermissionException || e instanceof ParamException){
                JsonData result= JsonData.fail(e.getMessage());
                mv=new ModelAndView("jsonView",result.toMap());
            }else{
                log.error("unknow json exception,url:"+url,e);
                JsonData result=JsonData.fail(defaultMsg);
                mv=new ModelAndView("jsonView",result.toMap());
             }
        }else if(url.endsWith(".page")){
            log.error("unknow page exception,url:"+url,e);
            //这里我们要求项目中所有请求json数据，都使用.page结尾
            JsonData result=JsonData.fail(defaultMsg);
            mv=new ModelAndView("exception",result.toMap());
        }else{
            log.error("unknow exception,url:"+url,e);
            JsonData result=JsonData.fail(e.getMessage());
            mv=new ModelAndView("jsonView",result.toMap());
        }
        return mv;
    }
}
