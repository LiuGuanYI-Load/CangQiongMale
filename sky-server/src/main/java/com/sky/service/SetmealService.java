package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;


import com.sky.entity.Setmeal;
import com.sky.result.PageResult;

import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface SetmealService {

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void saveWithDish(SetmealDTO setmealDTO);


    void deleteBatch(List<Long> ids);

    void startOrStop(Integer status, Long id);

    void updateWithDish(SetmealDTO setmealDTO);

    /**
     *  根据id查询套餐和菜品的关联关系
     * @param id
     * @return
     */
    SetmealVO getByIdWithDish(Long id);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);
}
