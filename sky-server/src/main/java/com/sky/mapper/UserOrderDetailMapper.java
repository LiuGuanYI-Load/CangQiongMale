package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserOrderDetailMapper {

    void insertBatch(List<OrderDetail> orderDetaillist) ;


    List<OrderDetail> getByOrderId(Long orderId);
}
