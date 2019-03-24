package service.sys.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import common.RequestHolder;
import dao.sys.SysRoleUserMapper;
import dao.sys.SysUserMapper;
import model.sys.SysRoleUser;
import model.sys.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import service.sys.SysRoleUserService;
import util.IpUtil;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleUserServiceImpl implements SysRoleUserService {

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysUser> getListByRoleId(int roleId) {
        List<Integer> userIdList= sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }

    @Override
    @Transactional
    public void changeRoleUsers(int roleId, List<Integer> userIdList) {
        List<Integer> origninUserIdList=sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if(origninUserIdList.size()==userIdList.size()){
            Set<Integer> originUserIdSet= Sets.newHashSet(origninUserIdList);
            Set<Integer> userIdSet=Sets.newHashSet(userIdList);
            originUserIdSet.removeAll(userIdSet);
            if(CollectionUtils.isEmpty(originUserIdSet)){
                return ;
            }
        }
        updateRoleUsers(roleId,userIdList);
    }

    @Transactional
     protected void updateRoleUsers(int roleId, List<Integer> userIdList) {
        sysRoleUserMapper.deleteByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            return ;
        }
        List<SysRoleUser> roleUserList=Lists.newArrayList();
        for(Integer userId:userIdList){
            SysRoleUser sysRoleUser=SysRoleUser.builder().roleId(roleId).userId(userId).operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                    .operateTime(new Date()).operator(RequestHolder.getCurrentUser().getUsername()).build();
            roleUserList.add(sysRoleUser);
        }

        sysRoleUserMapper.batchInsert(roleUserList);
    }
}
