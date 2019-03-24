package service.sys.impl;

import beans.PageQuery;
import beans.PageResult;
import com.google.common.base.Preconditions;
import common.RequestHolder;
import dao.sys.SysAclMapper;
import exception.ParamException;
import model.sys.SysAcl;
import org.springframework.stereotype.Service;
import param.AclParam;
import service.sys.SysAclService;
import util.BeanValidator;
import util.IpUtil;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclServiceImpl implements SysAclService {

    @Resource
    private SysAclMapper sysAclMapper;


    @Override
    public void save(AclParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(), param.getName(), param.getId())){
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl sysAcl= SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId())
                .url(param.getUrl()).type(param.getType()).status(param.getStatus()).seq(param.getSeq())
                .remark(param.getRemark()).build();
        sysAcl.setCode(generateCode());
        sysAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAcl.setOperateTime(new Date());
        sysAclMapper.insertSelective(sysAcl);
    }

    @Override
    public void update(AclParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(), param.getName(), param.getId())){
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl before=sysAclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限点不存在");
        SysAcl after=SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId())
                .url(param.getUrl()).type(param.getType()).status(param.getStatus()).seq(param.getSeq())
                .remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysAclMapper.updateByPrimaryKeySelective(after);
    }

    @Override
    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        int count=sysAclMapper.countByAclModuleId(aclModuleId);
        if(count>0){
            List<SysAcl> list=sysAclMapper.getPageByAclModuleId(aclModuleId,pageQuery);
            return PageResult.<SysAcl>builder().data(list).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }

    public boolean checkExist(int aclModuledId,String name,Integer id){
        return sysAclMapper.countByNameAndAclModuleId(aclModuledId,name,id)>0;
    }

    public String generateCode(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date())+"_"+(int)(Math.random()*100);
    }
}
