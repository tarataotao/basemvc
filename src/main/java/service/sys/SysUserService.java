package service.sys;

import beans.PageQuery;
import beans.PageResult;
import model.sys.SysUser;
import param.UserParam;

import java.util.List;

public interface SysUserService {

    public void save(UserParam param);

    public void update(UserParam param);

    SysUser findByKeyword(String keyword);

    PageResult<SysUser> getPageByDeptId(int deptId, PageQuery pageQuery);

    List<SysUser> getAll();
}
