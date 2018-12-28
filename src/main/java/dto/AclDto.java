package dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.sys.SysAcl;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString
public class AclDto extends SysAcl{

    // 是否要默认选中
    private boolean checked=false;

    //是否有权限操作
    private boolean hasAcl=false;

    public static AclDto aclDto(SysAcl acl){
        AclDto dto=new AclDto();
        BeanUtils.copyProperties(acl,dto);
        return dto;

    }
}