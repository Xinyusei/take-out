package com.sky.mapper;

import com.sky.entity.Orders;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    /**
     * 查询已完成订单的每日销售额总和
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    Double getAmountByOrderTimeAndSatus(Integer status, LocalDateTime beginTime, LocalDateTime endTime);

    Double getsumByMap(Map<Object, Object> map);


    List<Integer> getByOrderTime(LocalDateTime beginTime, LocalDateTime endTime);

    Integer getUserByTime(LocalDateTime beginTime, LocalDateTime endTime);


    Integer getNumByOrderTimeAndSatus(Integer status, LocalDateTime beginTime, LocalDateTime endTime);
}
