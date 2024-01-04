package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
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
    Double getByOrderTimeAndSatus(Integer status, LocalDateTime beginTime, LocalDateTime endTime);

    Double getsumByMap(Map<Object, Object> map);
}
