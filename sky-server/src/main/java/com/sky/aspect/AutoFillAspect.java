package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

//自定义注解  实现公共字段自动填充处理逻辑
@Aspect//AOP切面
@Component
@Slf4j//日志
public class AutoFillAspect {
    /**
     * 切入点-->对哪些类的哪些方法进行拦截
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")//第一个*后面必须有空格
    //包下所有.类.方法.参数类型  同时满足方法上加入了AutoFill注解
    public void autoFillPointCut() {
    }

    @Before("autoFillPointCut()")
    //前置通知  在通知之中进行公共字段的赋值
    public void autoFill(JoinPoint joinPoint) {
        //连接点 那个方法被拦截到了获取参数类型
        //反射 +AOP
        log.info("开始进行公共字段自动填充...");
        //获取到当前被拦截方法上的数据库操作类型
        //在Java编程语言中，方法签名（Method Signature）是一个重要概念，它包含了<--方法的名称、参数列表以及返回类型-->
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获得方法上的注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        //获得数据库操作类型
        OperationType operationType = autoFill.value();

        //获得当前被拦截方法的参数(此时的参数是个对象)-->实体对象
        Object[] args = joinPoint.getArgs();
        //获取到了Employee对象
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        //获取要更新的字段
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //如果上面抛出异常  下面的set语句就不会赋值  最后会出现全部为空
                //通过反射机制来为对象属性赋值
                //invoke把属性now currentId赋值给entity(Employee)
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射机制来为对象属性赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

}
