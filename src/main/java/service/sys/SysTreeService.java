package service.sys;

import dto.AclModuleLevelDto;
import dto.DeptLevelDto;

import java.util.List;

public interface SysTreeService {

    public List<DeptLevelDto> deptTree();

    public List<AclModuleLevelDto> aclModuleTree();

    List<AclModuleLevelDto> roleTree(int roleId);
}
