package service.sys.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import dao.sys.SysAclMapper;
import dao.sys.SysAclModuleMapper;
import dao.sys.SysDeptMapper;
import dto.AclDto;
import dto.AclModuleLevelDto;
import dto.DeptLevelDto;
import model.sys.SysAcl;
import model.sys.SysAclModule;
import model.sys.SysDept;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import service.sys.SysCoreService;
import service.sys.SysTreeService;
import util.LevelUtil;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysCoreService sysCoreService;
    @Resource
    private SysAclMapper sysAclMapper;

    @Override
    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList=sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList=Lists.newArrayList();
        for(SysAclModule sysAclModule:aclModuleList){
            dtoList.add(AclModuleLevelDto.adapt(sysAclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    @Override
    public List<AclModuleLevelDto> roleTree(int roleId) {
        //1.当前用户已经分配的权限点
        List<SysAcl> userAclList=sysCoreService.getCurrentUserAclList();
        //2.当前角色分配的权限点
        List<SysAcl> roleAclList=sysCoreService.getRoleAclList(roleId);
        //3.当前系统所有权限点
        List<AclDto> aclDtoList=Lists.newArrayList();

        Set<Integer> userAclIdSet=userAclList.stream().map(sysACl-> sysACl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet=roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        List<SysAcl> allAclList=sysAclMapper.getAll();//取出所有的权限点

        for(SysAcl acl:allAclList){
            AclDto dto=AclDto.aclDto(acl);
            if(userAclIdSet.contains(acl.getId())){
                dto.setHasAcl(true);
            }
            if(roleAclIdSet.contains(acl.getId())){
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if(CollectionUtils.isEmpty(aclDtoList)){
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelDtoList=aclModuleTree();
        Multimap<Integer,AclDto> moduleIdAclMap= ArrayListMultimap.create();
        for(AclDto acl:aclDtoList){
            if(acl.getStatus()==1){
                moduleIdAclMap.put(acl.getAclModuleId(),acl);
            }
        }
        bindAclsWithOrder(aclModuleLevelDtoList,moduleIdAclMap);
        return aclModuleLevelDtoList;
    }

    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelDtoList,Multimap<Integer,AclDto> moduleIdAclMap){
        if(CollectionUtils.isEmpty(aclModuleLevelDtoList)){
            return ;
        }
        for(AclModuleLevelDto dto:aclModuleLevelDtoList){
            List<AclDto> aclDtoList=(List<AclDto>)moduleIdAclMap.get(dto.getId());
            if(!CollectionUtils.isEmpty(aclDtoList)){
                Collections.sort(aclDtoList,aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(),moduleIdAclMap);
        }
    }

    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }

        Multimap<String,AclModuleLevelDto> levelAclModuleMap=ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList=Lists.newArrayList();
        for(AclModuleLevelDto aclModuleLevelDto:dtoList){
            levelAclModuleMap.put(aclModuleLevelDto.getLevel(),aclModuleLevelDto);
            if(LevelUtil.ROOT.equals(aclModuleLevelDto.getLevel())){
                rootList.add(aclModuleLevelDto);
            }
        }
        Collections.sort(rootList,aclModuleSeqComparator);

        transformAclModuleTree(rootList,LevelUtil.ROOT,levelAclModuleMap);

        return rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList,String level,Multimap<String,AclModuleLevelDto> levelAclModuleMap){

      for(int i=0;i<dtoList.size();i++){
          AclModuleLevelDto dto=dtoList.get(i);
          String nextLevel=LevelUtil.calculateLevel(level,dto.getId());
          List<AclModuleLevelDto> tempList= (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
          if(!CollectionUtils.isEmpty(tempList)){
              Collections.sort(tempList,aclModuleSeqComparator);
              dto.setAclModuleList(tempList);
              transformAclModuleTree(tempList,nextLevel,levelAclModuleMap);
          }
      }

    }

    @Override
    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList=sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList= Lists.newArrayList();
        for (SysDept sysdept:deptList) {
            DeptLevelDto dto=DeptLevelDto.adapt(sysdept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }

    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> dtoLevelList){
        if(CollectionUtils.isEmpty(dtoLevelList)){
            return Lists.newArrayList();
        }
        Multimap<String,DeptLevelDto> levelDeptMap= ArrayListMultimap.create();
        List<DeptLevelDto> rootList=Lists.newArrayList();
        for (DeptLevelDto dto:dtoLevelList) {
            levelDeptMap.put(dto.getLevel(),dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        //按照seq从小到大排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq()-o2.getSeq();
            }
        });
        //递归生成树
        transformDeptTree(dtoLevelList, LevelUtil.ROOT,levelDeptMap);
        return rootList;
    }

    //递归排序
    public void transformDeptTree(List<DeptLevelDto> deptLevelList,String level,Multimap<String,DeptLevelDto> levelDeptMap){
        for(int i=0;i<deptLevelList.size();i++){
            //遍历该层的每个元素
            DeptLevelDto deptLevelDto=deptLevelList.get(i);
            //处理当前层级的数据
            String nextLevel=LevelUtil.calculateLevel(level,deptLevelDto.getId());
            //处理下一层
            List<DeptLevelDto> tempDeptList= (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            if(!CollectionUtils.isEmpty(tempDeptList)){
                //排序
                Collections.sort(tempDeptList,deptSeqDtoComparator);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                //进入到下一层处理
                transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
            }
        }
    }


    public Comparator<DeptLevelDto> deptSeqDtoComparator=new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto> aclModuleSeqComparator=new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclDto> aclSeqComparator=new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };
}
