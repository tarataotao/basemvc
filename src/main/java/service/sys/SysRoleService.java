package service.sys;

import model.sys.SysRole;
import param.RoleParam;

import java.util.List;

public interface SysRoleService {

    public void save(RoleParam param);

    public void update(RoleParam param);

    public List<SysRole> getAll();

}
