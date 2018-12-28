package service.sys;

import java.util.List;

public interface SysRoleAclService {

    void changeRoleAcls(int roleId, List<Integer> aclIdList);
}
