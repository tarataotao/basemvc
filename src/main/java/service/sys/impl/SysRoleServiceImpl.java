package service.sys.impl;

import com.google.common.base.Preconditions;
import common.RequestHolder;
import dao.sys.SysRoleMapper;
import exception.ParamException;
import model.sys.SysRole;
import org.springframework.stereotype.Service;
import param.RoleParam;
import service.sys.SysRoleService;
import util.BeanValidator;
import util.IpUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public void save(RoleParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole sysRole=SysRole.builder().name(param.getName())
                .status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();
        sysRole.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysRole.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysRole.setOperateTime(new Date());
        sysRoleMapper.insertSelective(sysRole);
    }

    @Override
    public void update(RoleParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole befor=sysRoleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(befor,"待更新的角色不存在");
        SysRole after=SysRole.builder().id(param.getId()).name(param.getName())
                .status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysRoleMapper.updateByPrimaryKeySelective(after);
    }

    @Override
    public List<SysRole> getAll() {
        return sysRoleMapper.getAll();
    }

    private boolean checkExist(String name,Integer id){
        return sysRoleMapper.countByName(name,id)>0;
    }
}
