package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     * @param flavors
     */

    void insertBatch(List<DishFlavor> flavors);

    /**
     * 删除一条口味数据
     * 根据菜品id删除口味数据
     * @param dish_Id
     */
    @Delete("delete from dish_flavor where dish_id=#{dish_Id}")
    void deleteByDishId(Long dish_Id);

    /**
     * 删除多条口味数据
     *  根据菜品id删除口味数据
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据id查询口味数据
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}
