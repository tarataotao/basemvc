package common;

import model.sys.SysUser;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {

    private static final ThreadLocal<SysUser> userHolder=new ThreadLocal<SysUser>();

    private static final ThreadLocal<HttpServletRequest> requestHolder=new ThreadLocal<>();

    public static void add(SysUser sysUser){
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser(){
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest(){
        return requestHolder.get();
    }

    /**
     * 进程结束需要移除，否则一直占用内存，导致内存泄露
     */
    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }
}
