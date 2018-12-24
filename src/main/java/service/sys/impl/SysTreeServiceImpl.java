package service.sys.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import dao.sys.SysAclModuleMapper;
import dao.sys.SysDeptMapper;
import dto.AclModuleLevelDto;
import dto.DeptLevelDto;
import model.sys.SysAclModule;
import model.sys.SysDept;
import org.apache.ibatis.annotations.Result;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import service.sys.SysTreeService;
import util.LevelUtil;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Override
    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList=sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList=Lists.newArrayList();
        for(SysAclModule sysAclModule:aclModuleList){
            dtoList.add(AclModuleLevelDto.adapt(sysAclModule));
        }
        return aclModuleListToTree(dtoList);
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
        transformDeptTree(dtoLevelList,LevelUtil.ROOT,levelDeptMap);
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
}
