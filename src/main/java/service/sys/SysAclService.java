package service.sys;

import beans.PageQuery;
import beans.PageResult;
import model.sys.SysAcl;
import param.AclParam;

public interface SysAclService {

    public void save(AclParam aclParam);

    public void update(AclParam aclParam);

    PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery pageQuery);
}
