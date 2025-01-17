package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param //GlobalExceptionHandler
     * @return com.sky.result.Result
     * @author gangzi
     * @create 2024/8/18
     **/

    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
    /**
     * 捕获重复添加员工异常
     * @param //GlobalExceptionHandler
     * @return com.sky.result.Result
     * @author gangzi
     * @create 2024/8/18
     * //捕获SQL的异常
     **/


    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            //Duplicate entry ("123")  ()中的代表重复的username
            String[]split = message.split(" ");
            //按照" "分割 [2]位置就是重复的username
            String username=split[2];
            //将重复的username和提示信息拼接
            String msg = username+MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
