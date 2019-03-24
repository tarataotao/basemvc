package service.sys.impl;

import beans.PageQuery;
import beans.PageResult;
import com.google.common.base.Preconditions;
import common.RequestHolder;
import dao.sys.SysUserMapper;
import exception.ParamException;
import model.sys.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import param.UserParam;
import service.sys.SysUserService;
import util.BeanValidator;
import util.IpUtil;
import util.MD5Util;
import util.PasswordUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void save(UserParam param) {
        BeanValidator.check(param);
        if(checkEmailExist(param.getMail(),param.getId())){
            throw  new ParamException("电话已被占用");
        }

        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("邮箱已经被占用");
        }

        String password= PasswordUtil.randomPassword();
        password="12345678";
        String encryptedPassword= MD5Util.encrypt(password);
        SysUser sysUser= SysUser.builder().username(param.getUsername()).telephone(param.getTelephone())
                .password(encryptedPassword).mail(param.getMail()).password(password).deptId(param.getDeptId())
                .status(param.getStatus()).remark(param.getRemark()).build();
        sysUser.setOperateIp(RequestHolder.getCurrentUser().getUsername());
        sysUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysUser.setOperateTime(new Date());
        //TODO:sendEmail
        sysUserMapper.insertSelective(sysUser);
    }

    @Override
    @Transactional
    public void update(UserParam param){
        BeanValidator.check(param);
        if(checkEmailExist(param.getMail(),param.getId())){
            throw  new ParamException("邮箱已经被占用");
        }

        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }

        SysUser before=sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");
        SysUser after=SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).deptId(param.getDeptId()).status(param.getStatus())
                .remark(param.getRemark()).build();
        after.setOperateIp(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(after);

    }

    @Override
    public SysUser findByKeyword(String keyword) {
        return sysUserMapper.findByKeyword(keyword);
    }

    @Override
    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        int count=sysUserMapper.countByDeptId(deptId);
        if(count>0){
            List<SysUser> list=sysUserMapper.getPageByDeptId(deptId,pageQuery);
            return PageResult.<SysUser>builder().data(list).total(count).build();
        }
        return PageResult.<SysUser>builder().build();
    }

    @Override
    public List<SysUser> getAll() {
        return sysUserMapper.getAll();
    }

    public boolean checkEmailExist(String mail,Integer userId){
       return sysUserMapper.countByMail(mail,userId)>0;
    }

    public boolean checkTelephoneExist(String telephone,Integer userId){
        return sysUserMapper.countByTelephone(telephone,userId)>0;
    }
}
