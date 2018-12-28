package service.sys.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import common.RequestHolder;
import dao.sys.SysRoleAclMapper;
import model.sys.SysRoleAcl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import service.sys.SysRoleAclService;
import util.IpUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleAclServiceImpl implements SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Override
    @Transactional
    public void changeRoleAcls(int roleId, List<Integer> aclIdList) {
        List<Integer> originAclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if(originAclIdList.size()==aclIdList.size()){
            //看看当前的是不是跟原来的一样，如果一样的话则不需要做任何操作
            Set<Integer> originAclIdSet= Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet= Sets.newHashSet(aclIdList);
            originAclIdSet.removeAll(aclIdSet);
            if(CollectionUtils.isEmpty(originAclIdList)){
                return;
            }
        }
        updateRoleAcls(roleId, aclIdList);

    }

    @Transactional
    public void updateRoleAcls(int roleId,List<Integer> aclIdList){
        sysRoleAclMapper.deleteByRoleId(roleId);

        if(CollectionUtils.isEmpty(aclIdList)){
            return;
        }

        List<SysRoleAcl> roleAclList=Lists.newArrayList();
        for(Integer aclId:aclIdList){
            SysRoleAcl roleAcl=SysRoleAcl.builder().roleId(roleId).aclId(aclId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleAclList.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }
}
