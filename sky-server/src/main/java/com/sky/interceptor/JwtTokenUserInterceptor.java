package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源


        //登陆请求放行  就可以在拦截器的拦截路径中不指定login
//        String url = request.getRequestURI();
//        if(url.contains("login")){
//            return true;
//        }


        //if (!(handler instanceof HandlerMethod)):
        //
        //这段代码用于判断当前的 handler 是否是一个 HandlerMethod 实例。
        // HandlerMethod 表示的是控制器中的方法（通常是一个处理请求的方法）。
        //如果 handler 不是 HandlerMethod，说明当前请求处理的可能不是一个动态的控制器方法，
        // 而是一个静态资源（如图片、CSS、JavaScript 文件等）。
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        //在请求的时候，请求头中携带的令牌名称默认是token
        String token = request.getHeader(jwtProperties.getUserTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            //有token和secretKey就可以校验  secretKey是jwt的密钥(元数据) 可以在配置文件中配置
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            //登陆生成令牌的时候，将用户id放入了claims中
            Long UserId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", UserId);
            //在ThreadLocal来存储当前用户id
            BaseContext.setCurrentId(UserId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
