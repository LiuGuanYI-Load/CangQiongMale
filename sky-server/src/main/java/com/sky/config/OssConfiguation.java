package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguation {
    /**
     *
     * 加上Bean注解这样当项目启动时就创建出这个对象
     * 也可以直接在 AliOssUtils上加@Component
     * 之后在AliOssUtils注入Aliyunossproperties
     * 之后调用getEndpoint方法
     * @param //OssConfiguation
     * @return com.sky.utils.AliOssUtil
     * @author gangzi
     * @create 2024/8/20
     **/

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象:{}",aliOssProperties);
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());

    }


}
