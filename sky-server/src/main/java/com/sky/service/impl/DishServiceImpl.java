package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品以及口味
     * 涉及两张表所以加入事务回滚  启动类已经加过
     * @param //DishServiceImpl
     * @return void
     * @author gangzi
     * @create 2024/8/21
     **/
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //向菜品表加入数据
        dishMapper.insert(dish);
        //获取到菜品id  然后才能在口味表插入数据
        //在insert方法下编写主键回显 把id回显到dish对象中
        Long dishId = dish.getId();
        //向口味表加入n条数据
        List<DishFlavor> flavors =dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            //遍历flavor设置插入菜品的id
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
        dishFlavorMapper.insertBatch(flavors);
        }

    }
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        //查询分页数据
       Page<DishVO> page =dishMapper.pageQuery(dishPageQueryDTO);
       //返回封装后的数据
        //Long total = page.getTotal();
        //List<DishVO> records = page.getResult();
        //return  new PageResult(total,records);
       return new PageResult(page.getTotal(),page.getResult());
    }
    @Transactional
    public void deleteById(List<Long> ids) {
        //1,是否存在起售中的菜品

        //判断菜品是否起售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus()==StatusConstant.ENABLE){
                //起售菜品不能删除
                //抛出自定义异常
                throw new DeletionNotAllowedException(dish.getName()+"是"+MessageConstant.DISH_ON_SALE);
            }
        }
        //2,是否被套餐关联
//        if(dishMapper.countByCategoryId(ids)>0){
//            //被套餐关联不能删除
//            //抛出自定义异常
//            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
//        }
        //2,是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds!=null && setmealIds.size()>0){
            //抛出关联了菜品 无法删除的异常
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品
//        for(Long id:ids) {
//            dishMapper.deleteById(id);
//            dishFlavorMapper.deleteByDishId(id);
//        }
//        发的请求太多
        //批量删除菜品和口味-->根据传进来的集合删除
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);

        //删除菜品口味
    }

    /**
     * 根据id查询口味和菜品信息
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品
        Dish dish = dishMapper.getById(id);
        //根据id查询口味  封装在List集合  因为口味可能不止一种
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //创建要返回的VO对象
        DishVO dishVO = new DishVO();
        //拷贝查询到的dish对象信息到dishVO对象中
        BeanUtils.copyProperties(dish,dishVO);
        //把查询到的口味集合设置到dishVO对象中
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品表基本信息(DTO中还有flavor口味)
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //修改口味表
        // 得到dishDTO传进来的id 因为 前端有可能追加 有可能没改动 有可能设为空 先删除口味表=
            dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //再添加口味表
        //批量插入 和新增逻辑相同
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            for (DishFlavor flavor : flavors) {
                //设置id
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }
    public void startOrStop(Integer status, Long id){
        Dish dish = Dish.builder().status(status).id(id).build();
        dishMapper.update(dish);
    }

    /**
     *  根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }



    /**
     * user条件查询菜品和口味
     * @param dish
     * @return
     */

    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
