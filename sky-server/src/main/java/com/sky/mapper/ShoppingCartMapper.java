package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
     /**
      * 动态条件查询购物车数据
      * @param shoppingCart
      * @return
      */
     List<ShoppingCart> list(ShoppingCart shoppingCart);
     /**
      * 动态修改购物车数据
      * @param cart
      */
     @Update("update shopping_cart set number=#{number} where id=#{id}")
     void updateNumberById(ShoppingCart cart);
     @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) " +
             "values( #{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime})")
     void insert(ShoppingCart shoppingCart);
     @Delete("delete from shopping_cart where user_id=#{UserId} ")
     void deleteByUserId(Long UserId);
     @Delete("delete from shopping_cart where id=#{id}")
     void deleteById(Long id);


     void insertBatch(List<ShoppingCart> shoppingCartList);
}
