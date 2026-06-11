package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {

    /**
     * 插入訂單數據
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根據訂單號碼查詢訂單
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改訂單資訊
     * @param orders
     */
    void update(Orders orders);


    /**
     * 測試訂單跳過微信小程序的方法
     * @param orderStatus
     * @param orderPaidStatus
     * @param check_out_time
     * @param id
     */
    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);
}
