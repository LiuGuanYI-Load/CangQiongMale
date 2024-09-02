package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //通过建立数据库表的方式来添加购物车
        //如果购物车中已经存在该菜品或者套餐，则数量加一，否则添加到购物车中
        ShoppingCart shoppingCart = new ShoppingCart();
        //拷贝
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //取出ThreadLocal中的用户id
        Long Id= BaseContext.getCurrentId();
        //设置用户id
        shoppingCart.setUserId(Id);
        //查询当前菜品或者套餐是否在购物车中
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        //没查出来 则添加
        if(list.size()>0 && list!=null){
            ShoppingCart Cart=list.get(0);
            Cart.setNumber(Cart.getNumber()+1);
        //更新number根据id  number是+1之后的
            shoppingCartMapper.updateNumberById(Cart);
        }else{
            //获取到购物车中的菜品id
            Long dishid=shoppingCart.getDishId();
            if(dishid!=null){
                //获取到菜品
                Dish dish=dishMapper.getById(dishid);
                //金额
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());

            }else{
                Long setmealid=shoppingCart.getSetmealId();
                Setmeal setmeal=setmealMapper.getById(setmealid);
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);

        }
        //购物车中添加菜品或者套餐
    }


    public List<ShoppingCart> showshoppingCart() {
        ShoppingCart shoppingCart= ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //删除购物车中一个菜品
        ShoppingCart shoppingcart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingcart);
        shoppingcart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingcart);
        //if(list.size()>0&& list!=null){
            ShoppingCart cart=list.get(0);
            Integer number=cart.getNumber();
            if(number==1){
                shoppingCartMapper.deleteById(cart.getId());
            }else{
                shoppingcart.setNumber(number-1);
                shoppingCartMapper.updateNumberById(shoppingcart);
            }

       // }


    }
}
