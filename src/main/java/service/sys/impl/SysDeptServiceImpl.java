package service.sys.impl;

import com.google.common.base.Preconditions;
import common.RequestHolder;
import dao.sys.SysDeptMapper;
import dao.sys.SysUserMapper;
import exception.ParamException;
import model.sys.SysDept;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import param.DeptParam;
import service.sys.SysDeptService;
import util.BeanValidator;
import util.IpUtil;
import util.LevelUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptServiceImpl implements SysDeptService{

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysUserMapper sysUserMapper;


    @Override
    public void save(DeptParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept sysDept= SysDept.builder().name(param.getName())
                .parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        sysDept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        sysDept.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysDept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysDept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(sysDept);
    }

    @Override
    @Transactional
    public void update(DeptParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept before=sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的部门不存在");
        SysDept after=SysDept.builder().id(param.getId()).name(param.getName())
                .parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before,after);
    }

    @Override
    public void delete(int deptId) {
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(dept,"待删除的部门不存在，无法删除");
        int abc=sysDeptMapper.countByParentId(deptId);
        if(sysDeptMapper.countByParentId(dept.getId())>0){
            throw new ParamException("当前部门下面有子部门，无法删除");
        }
        if(sysUserMapper.countByDeptId(dept.getId())>0){
            throw new ParamException("当前部门下面有用户，无法删除");
        }
        sysDeptMapper.deleteByPrimaryKey(deptId);

    }

    private void updateWithChild(SysDept befor,SysDept after){
        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=befor.getLevel();
        if(!newLevelPrefix.equals(oldLevelPrefix)){
            List<SysDept> deptList=sysDeptMapper.getChildDeptListByLevel(oldLevelPrefix);
            if(CollectionUtils.isEmpty(deptList)){
                for(SysDept dept:deptList){
                    String level=dept.getLevel();
                    if(level.indexOf(oldLevelPrefix)==0){
                        level=newLevelPrefix+level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);
    }

    private boolean checkExist(Integer parentId,String deptName,Integer deptId){

        int count = sysDeptMapper.countByNameAndParentId(parentId,deptName,deptId);
        return count>0?true:false;
    }

    private String getLevel(Integer deptId){
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept==null){
            return null;
        }
        return dept.getLevel();
    }
}
