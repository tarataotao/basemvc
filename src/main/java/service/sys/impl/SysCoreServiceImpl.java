package service.sys.impl;

import com.google.common.collect.Lists;
import common.RequestHolder;
import dao.sys.SysAclMapper;
import dao.sys.SysRoleAclMapper;
import dao.sys.SysRoleMapper;
import dao.sys.SysRoleUserMapper;
import model.sys.SysAcl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import service.sys.SysCoreService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysCoreServiceImpl implements SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Override
    public List<SysAcl> getCurrentUserAclList() {
        int userId= RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    @Override
    public List<SysAcl> getRoleAclList(int roleId) {
         List<Integer> aclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if(CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }

    @Override
    public List<SysAcl> getUserAclList(int userId) {
        if(isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        List<Integer> userRoleIdList=sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> userAclList=sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if(CollectionUtils.isEmpty(userAclList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclList);
    }

    @Override
    public boolean isSuperAdmin() {
        return true;
    }
}
