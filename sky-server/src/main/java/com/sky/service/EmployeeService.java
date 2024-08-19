package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     *
     *  新增员工
     * @param employeeDTO
     * @return void
     * @author gangzi
     * @create 2024/8/16
     **/

    void save(EmployeeDTO employeeDTO);
/**
 * 分页查询
 * @param //EmployeeService
 * @return com.sky.result.PageResult
 * @author gangzi
 * @create 2024/8/18
 **/

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

/**
 * 状态设置
 * @param //EmployeeService
 * @return void
 * @author gangzi
 * @create 2024/8/18
 **/

void startOrStop(Integer status, Long id);

/**
 * 根据id查询员工信息
 * @param //EmployeeService
 * @return com.sky.entity.Employee
 * @author gangzi
 * @create 2024/8/19
 **/

    Employee getById(Long id);
    /**
     * 修改员工信息
     * @param //EmployeeService
     * @return void
     * @author gangzi
     * @create 2024/8/19
     **/

    void update(EmployeeDTO employeeDTO);
}
