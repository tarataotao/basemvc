package service.sys;

import model.sys.SysAcl;

import java.util.List;

public interface SysCoreService {
    List<SysAcl> getCurrentUserAclList();

    List<SysAcl> getRoleAclList(int roleId);

    /**
     * 查询某个用户的所有权限
     * @param userId
     * @return
     */
    List<SysAcl> getUserAclList(int userId);

    /**
     * 是否是超级用户
     * @return
     */
    public boolean isSuperAdmin();
}
