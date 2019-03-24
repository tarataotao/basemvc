package service.sys;

import param.DeptParam;

public interface SysDeptService {

    public void save(DeptParam param);

    void update(DeptParam param);

    void delete(int deptId);
}
