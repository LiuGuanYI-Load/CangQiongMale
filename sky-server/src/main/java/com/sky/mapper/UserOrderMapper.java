package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserOrderMapper {



    //    @Insert("insert into orders( number, status, user_id, address_book_id, pay_method, order_time, pay_status, amount, phone, address, remark, cancel_reason, rejection_reason, estimated_delivery_time, delivery_status, delivery_time, cancel_time, remark) values ( #{number}, #{status}, #{userId}, #{addressBookId}, #{payMethod}, #{orderTime}, #{payStatus}, #{amount}, #{phone}, #{address}, #{remark}, #{cancelReason}, #{rejectionReason}, #{estimatedDeliveryTime}, #{deliveryStatus}, #{deliveryTime}, #{cancelTime}, #{remark})")
//    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    //这个注解的意思就是，从 keyColumn这个字段里面把数据放到传入对象的ikeyProperty成员变量里面。

    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);
    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    @Select("select  *from order_detail  where order_id=#{id}")
    Orders getById(Long id);
}
