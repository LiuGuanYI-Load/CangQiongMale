package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品查询套餐id
     * @param dishIds
     * @return
     */
    // select setmeal_id from setmeal_dish where dish_id in (1,2,3)  拼接(1,2,3)需要括号
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDishes);


    void deleteBySetmealId(Long setmealId);

//根据套餐id查询套餐菜品关系数据  只从套餐菜品表中查询
    List<SetmealDish> getBySetmealId(Long setmeal_dishId);


}
