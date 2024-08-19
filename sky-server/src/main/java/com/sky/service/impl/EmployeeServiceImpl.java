package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {


    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //密码比对
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        //3、返回实体对象
        return employee;
    }
    /**
     *
     * 新增员工
     * @param employeeDTO
     * @return void
     * @author gangzi
     * @create 2024/8/16
     **/

    @Override
    public void save(EmployeeDTO employeeDTO) {
        //传进来DTO是为了方便封装前端提交的数据
        //调用持久层Mapper
        //此处需要将DTO转为实体
       Employee employee= new Employee();
       //employee.setName(employeeDTO.getName());
        //太繁琐
        //使用Spring提供的BeanUtils来一键拷贝属性-->条件是DTO中的属性名和employee之中必须一样
        BeanUtils.copyProperties(employeeDTO,employee);
        //拷贝DTO到实体之中
        //employeeDTO没有的属性自己设置

        //设置账号的状态1 表示正常  0表示锁定
        employee.setStatus(StatusConstant.ENABLE);
        //设置密码(dto无得属性
        //新账号的进入必须设置默认密码
        //建立一个常量类
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置当前记录时间的创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //设置当前记录创建人id和修改人id  可能jwt
//        // TODO 后期改 当前登录的用户
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insert(employee);
    }
    /**
     * 分页查询逻辑处理层方法-->使用pagehelper
     * @param //EmployeeServiceImpl
     * @return com.sky.result.PageResult
     * @author gangzi
     * @create 2024/8/18
     **/

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询
        //分页查询的结果封装成 PageResult 类
        //selcet*from employee limit 0,10  -->第几页 ""开始每一页几条
        //省去了XMl文件之中 limit查询语句
        //startPage的作用就是底层调用ThreadLocal  存储当前输入的Page分页参数  等到XMl里面再拼接
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        //传进来employeePageQueryDTO  得使用DTO里面的员工姓名
        //pageQuery 方法返回的对象是 Page(已经存在)类型
        Page<Employee>page=employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        //得到的数据传进PageResult   总共的数据量  还有查询到的数据集合
        //PageHelper还可以查询总的数据量
        List<Employee> records= page.getResult();
        return new PageResult(total,records);
    }
    /**
     * 禁用
     * @param //EmployeeServiceImpl
     * @return com.sky.mapper.EmployeeMapper
     * @author gangzi
     * @create 2024/8/18
     **/

    public void startOrStop(Integer status, Long id) {
        //update employee set status =? where id =?
        /*Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);*/

        //链式调用  每个构造方法返回当前对象就能一直调用下去
        Employee employee = Employee.builder().status(status).id(id).build();
        //方法名字设置为update其他的路径也能用
        employeeMapper.update(employee);

    }


    /**
 *  根据id查询员工信息
 * @param //EmployeeServiceImpl
 * @return com.sky.entity.Employee
 * @author gangzi
 * @create 2024/8/19
 **/

    public Employee getById(Long id) {
        Employee employee =employeeMapper.getById(id);
        employee.setPassword("******");//简单加密  不让前端看
        return employee;
    }

    /**
     * 更改员工信息
     * @param //EmployeeServiceImpl
     * @return void
     * @author gangzi
     * @create 2024/8/19
     **/

    public void update(EmployeeDTO employeeDTO) {
        //之前编写的mappering是Employee类型
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
//        //除了拷贝的属性还要更新updateuser updatetime(当前时间)
//        employee.setUpdateTime(LocalDateTime.now());
//        //Id是Jwt校验时通过BaseConTxet(底层ThreadLocal)存储在线程存储空间
//        employee.setUpdateUser(BaseContext.getCurrentId());
        //修改
        employeeMapper.update(employee);
    }
}
