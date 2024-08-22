package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
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
}
