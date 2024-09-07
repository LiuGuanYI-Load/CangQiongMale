package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {


    //    @Insert("insert into orders( number, status, user_id, address_book_id, pay_method, order_time, pay_status, amount, phone, address, remark, cancel_reason, rejection_reason, estimated_delivery_time, delivery_status, delivery_time, cancel_time, remark) values ( #{number}, #{status}, #{userId}, #{addressBookId}, #{payMethod}, #{orderTime}, #{payStatus}, #{amount}, #{phone}, #{address}, #{remark}, #{cancelReason}, #{rejectionReason}, #{estimatedDeliveryTime}, #{deliveryStatus}, #{deliveryTime}, #{cancelTime}, #{remark})")
//    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    //这个注解的意思就是，从 keyColumn这个字段里面把数据放到传入对象的ikeyProperty成员变量里面。

    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select  *from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计订单数量
     *
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status =#{status} and order_time<#{orderTime}")
    List<Orders> getOrderByStatusTimeout(Integer status, LocalDateTime orderTime);

    Double sumByMap(Map map);

    Integer countByMap(Map map);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
    //根据动态条件统计营业额数据
}
