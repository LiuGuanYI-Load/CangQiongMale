package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@Slf4j
@Api(tags="菜品管理")
@RequestMapping("/admin/dish")
@RestController
public class DishController {
    @Autowired DishService dishService;
    /**
     * 新增菜品
     * @param //DishController
     * @return com.sky.result.Result
     * @author gangzi
     * @create 2024/8/21
     **/
    @ApiOperation("新增菜品")
    @PostMapping()
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
    /**
     * 根据id查询菜品
     * @param //DishController
     * @return com.sky.result.Result<com.sky.vo.DishVO>
     * @author gangzi
     * @create 2024/8/22
     **/
    @ApiOperation("根据id查询菜品以及口味")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品信息:{}",id);
        DishVO dishVO=dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询-->参数:{}",dishPageQueryDTO);
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        //用数组也可以Long[]ids 字符串也可以String ids
        log.info("批量删除菜品-->参数:{}",ids);
        dishService.deleteById(ids);
        return Result.success();
    }
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品-->参数:{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售菜品")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("起售停售菜品:{},{}",status,id);
        dishService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 在新增套餐时需要根据套餐id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类id查询套餐：{}", categoryId);
        List<Dish> list=dishService.list(categoryId);
        return Result.success(list);
    }


}
