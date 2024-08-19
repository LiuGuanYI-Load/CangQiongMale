package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags="员工相关接口")//主要是给前端看的 -->通过注解写前端页面
public class EmployeeController {
    //查询类操作就加上<T>泛型  因为返回data
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value ="员工登入")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value ="员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     *
     *  新增员工
     * @param employeeDTO
     * @return com.sky.result.Result
     * @author gangzi
     * @create 2024/8/16
     **/


    @PostMapping()
    @ApiOperation(value="新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工:{}",employeeDTO);
        employeeService.save(employeeDTO);//alt+enter创建方法
        return Result.success();
        //开发阶段 前后端并行开发  所以主要以接口文档测试为主

    }

    /**
     *
     * @param //EmployeeController
     * @return com.sky.result.Result<com.sky.result.PageResult>
     * @author gangzi
     * @create 2024/8/18
     **/

    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    //query格式
    public Result<PageResult>page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询,参数为:{}",employeePageQueryDTO);
        PageResult pageResult=employeeService.pageQuery(employeePageQueryDTO);
        //传入pageResult
        //最后将 pageResult 封装到 Result 返回前端
        return Result.success(pageResult);
    }

    /**
     * 启动禁用员工账号
     * @param //EmployeeController
     * @return com.sky.result.Result
     * @author gangzi
     * @create 2024/8/18
     * {status}-->路径参数需在url上加
     **/

    @PostMapping("/status/{status}")
    @ApiOperation("启动禁用员工账号")
    public Result startOrStop(@PathVariable("status")Integer status,Long id){
        log.info("启动禁用员工账号:{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

}
