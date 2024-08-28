package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();

        BeanUtils.copyProperties(setmealDTO, setmeal);
        //设置套餐状态(setmealDTo中默认为0 -->既停售)
        //setmeal.setStatus(StatusConstant.ENABLE);
        //向套餐表插入数据
        setmealMapper.insert(setmeal);
        //获取生成的套餐id
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        //保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     *  批量删除套餐
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids){
        for(Long id:ids){
            Setmeal setmeal=setmealMapper.getById(id);
            if(setmeal.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(id+"是"+MessageConstant.SETMEAL_ON_SALE);
            }
        }

        for(Long id:ids) {
            //删除套餐表中的数据
            setmealMapper.deleteById(id);
            //删除套餐菜品关系表中的数据
            setmealDishMapper.deleteBySetmealId(id);
        }
    }
   public void startOrStop(Integer status, Long id){
        //起售套餐status == StatusConstant.ENABLE需要判断套餐内的菜品是否都是起售
       if(status == StatusConstant.ENABLE){
           //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
           //根据套餐id找出套餐中包含的菜品
           List<Dish> dishList = dishMapper.getBySetmealId(id);
           if(dishList != null && dishList.size() > 0){
               dishList.forEach(dish -> {
                   if(StatusConstant.DISABLE == dish.getStatus()){
                       throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                   }
               });
           }
       }

       Setmeal setmeal = Setmeal.builder()
               .id(id)
               .status(status)
               .build();
       setmealMapper.update(setmeal);
    }
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO){
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        ////2、删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());

        //重新插入套餐和菜品的关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDTO.getId());
        });
        //重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
        //setmealDishes表中是套餐中的餐品信息
        setmealDishMapper.insertBatch(setmealDishes);
    }


    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal=setmealMapper.getById(id);
        //获取套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        //拷贝信息从setmeal到setmealVO
        BeanUtils.copyProperties(setmeal, setmealVO);
        //设置套餐菜品
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;

    }
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }
    public List<DishItemVO> getDishItemById(Long id){
        return setmealMapper.getDishItemById(id);
    }
}
