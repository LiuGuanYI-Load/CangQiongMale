package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    public static  final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    public User WxLogin(UserLoginDTO userLoginDTO) {
        //将原本获取openid的代码封装到方法中
        //得到openid
        String openid=getOpenid(userLoginDTO.getCode());
        if(openid==null){
            throw  new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user=userMapper.getByOpenid(openid);
        //没有注册过的用户  在数据库中插入一条数据
        if(user==null){
            user=User
                .builder()
                .openid(openid)
                .createTime(LocalDateTime.now())
                .build();
            //插入数据并且拿到id到user对象  id是MyBatis 使用数据库生成的键值一数据的自增主键
            userMapper.insert(user);
        }

        return user;

    }
    private  String getOpenid(String code){
        Map<String,String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        //得到传进来的微信code
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        //把使用HttpCilentUtil发送请求到微信服务器得到的json字符串(包含openid)
        //解析出openid
        String josn=HttpClientUtil.doGet(WX_LOGIN,map);
        //解析得到的json  发送请求到微信返回的josn
        JSONObject jsonObject=JSONObject.parseObject(josn);
        String openid=jsonObject.getString("openid");
        return openid;
    }
}
