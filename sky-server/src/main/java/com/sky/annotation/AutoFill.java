package com.sky.annotation;

import com.sky.enumeration.OperationType;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

        /**
         *
         * @param //AutoFill
         * @return自定义注解
         * @author gangzi
         * @create 2024/8/19
         **/

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AutoFill{
            //指定属性 -->当前数据库操作的类型 update insert
            //update_time update_user create_time create_user
            //这几个字段每次insert update就会更新 但是每次都得设置
            OperationType value();
            //自定义 Operation类

        }


