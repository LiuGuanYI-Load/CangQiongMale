package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags="店铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
public static final String SHOP_STATUS = "SHOP_STATUS";
    /**
     * 使用Redis设置店铺的营业状态
     * @param status
     * @return
     */
    @ApiOperation("设置店铺的营业信息")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺的营业状态:{}",status==1? "营业中":"打烊中");
        redisTemplate.opsForValue().set(SHOP_STATUS,status);
        return Result.success();
    }

    /**
     * 查询店铺的营业状态
     * @return
     */
    @ApiOperation("获取店铺的营业信息")
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        log.info("查询店铺的营业状态:{}",status==1? "营业中":"打烊中");
        return Result.success(status);
    }
}
