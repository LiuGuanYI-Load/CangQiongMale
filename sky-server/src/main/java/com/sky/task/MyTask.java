package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class MyTask {
    @Autowired
    private OrderMapper orderMapper;
//    @Scheduled(cron="0/5 * * * * ?")
    @Scheduled(cron ="0 * * * * ? ")
    public void processTimeoutOrder(){
        log.info("付款超时订单开始执行:");
        LocalDateTime time =LocalDateTime.now().plusMinutes(-15);
        List<Orders> list=orderMapper.getOrderByStatusTimeout(Orders.PENDING_PAYMENT,time);
        if(list!=null && list.size()>0){
            for(Orders order :list){
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason("订单超时取消");
                orderMapper.update(order);
            }
        }
    }
//    @Scheduled(cron="0/5 * * * * ?")
    @Scheduled(cron="0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("未完成订单开始执行:");
        LocalDateTime time =LocalDateTime.now().plusMinutes(-60);
        List<Orders> list=orderMapper.getOrderByStatusTimeout(Orders.DELIVERY_IN_PROGRESS,time);
        if(list!=null&&list.size()>0){
            for(Orders order :list){
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }

}
