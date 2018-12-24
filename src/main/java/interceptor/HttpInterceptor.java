package interceptor;

import common.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import util.JsonMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {

    private static final String START_TIME="requestStartTime";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url=request.getRequestURI().toString();
        Map parameterMap=request.getParameterMap();
        log.info("request start .url:{}，params:{}",url, JsonMapper.obj2String(parameterMap));
        long start=System.currentTimeMillis();
        request.setAttribute(START_TIME,start);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
      /*  String url=request.getRequestURI().toString();
        Map parameterMap=request.getParameterMap();
        long start= (long) request.getAttribute(START_TIME);
        long end=System.currentTimeMillis();

        log.info("request finished .url:{}，params:{},coust:{}",url, JsonMapper.obj2String(parameterMap),(end-start));
*/
        removeThreadLocalInfo();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url=request.getRequestURI().toString();
        Map parameterMap=request.getParameterMap();
        long end=System.currentTimeMillis();
        long start= (long) request.getAttribute(START_TIME);
        log.info("request compelete .url:{}，params:{}.cost:{}",url, JsonMapper.obj2String(parameterMap),(end-start));
        removeThreadLocalInfo();
    }

    public void removeThreadLocalInfo(){
        RequestHolder.remove();
    }
}
