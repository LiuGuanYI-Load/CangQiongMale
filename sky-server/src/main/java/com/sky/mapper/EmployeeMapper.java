package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据 持久层
     * @param employee
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) " +
            "values"+"(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value= OperationType.INSERT)
    void insert( Employee employee);

    /**
     * 分页查询-->动态sql -->XML映射文件
     * @param //EmployeeMapper
     * @return com.github.pagehelper.Page<com.sky.entity.Employee>
     * @author gangzi
     * @create 2024/8/18
     **/
    //因为使用了 PageHelper 插件所以返回值必须设置为 Page
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);



    /**
     * 动态修改属性
     * @param //EmployeeMapper
     * @return void
     * @author gangzi
     * @create 2024/8/18
     **/
    @AutoFill(value= OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     * @param //EmployeeMapper
     * @return com.sky.entity.Employee
     * @author gangzi
     * @create 2024/8/19
     **/

    @Select("select *from employee where id=#{id}")
    Employee getById(Long id);
}
