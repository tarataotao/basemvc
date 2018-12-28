package service.sys.impl;

import com.google.common.base.Preconditions;
import common.RequestHolder;
import dao.sys.SysAclModuleMapper;
import exception.ParamException;
import model.sys.SysAclModule;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import param.AclModuleParam;
import service.sys.SysAclModuleService;
import util.BeanValidator;
import util.IpUtil;
import util.LevelUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleServiceImpl implements SysAclModuleService {

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Override
    public void save(AclModuleParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("统一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule=SysAclModule.builder().name(param.getName()).parentId(param.getParentId()).seq(param.getSeq())
                .status(param.getStatus()).remark(param.getRemark()).build();
        String level= LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId());
        aclModule.setLevel(level);
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(aclModule);
    }

    @Override
    public void update(AclModuleParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule before=sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限模块不存在");
        SysAclModule after=SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId()).seq(param.getSeq())
                .status(param.getStatus()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before,after);
    }

    private void updateWithChild(SysAclModule before,SysAclModule after){
        String newLevelPrefix=after.getLevel();
        String oldLevelPreFix=before.getLevel();
        if(!after.getLevel().equals(before.getLevel())){
            List<SysAclModule> aclModuleList= sysAclModuleMapper.getChildAclModuleListByLevel(before.getLevel());
            if(!CollectionUtils.isEmpty(aclModuleList)){
                for (SysAclModule sysAclModule:aclModuleList){
                    String level=sysAclModule.getLevel();
                    if(level.indexOf(oldLevelPreFix)==0){
                        level=newLevelPrefix+level.substring(oldLevelPreFix.length());
                        sysAclModule.setLevel(level);
                    }
                }
            }
            sysAclModuleMapper.batchUpdateLevel(aclModuleList);
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);
    }


    private boolean checkExist(Integer parentId,String aclModuleName,Integer deptId){
        return sysAclModuleMapper.countByNameAndParentId(parentId,aclModuleName,deptId)>0;
    }

    private String getLevel(Integer aclModuleId){
        SysAclModule sysAclModule=sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if(sysAclModule==null){
            return null;
        }
        return sysAclModule.getLevel();
    }
}
