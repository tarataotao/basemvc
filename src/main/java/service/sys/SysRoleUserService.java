package service.sys;

import model.sys.SysUser;

import java.util.List;

public interface SysRoleUserService {

    List<SysUser> getListByRoleId(int roleId);

    void changeRoleUsers(int roleId, List<Integer> userIdList);
}
