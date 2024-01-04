package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    private ReportMapper reportMapper;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {

        //日期列表
        ArrayList<LocalDate> dateList = new ArrayList<>();
        //营业额列表
        ArrayList<Double> turnoverList = new ArrayList<>();
        LocalDate date = begin;
        Integer status = Orders.COMPLETED;
        while (!date.equals(end)) {
            dateList.add(date);
            //TODO 这里的begin是LocalDate类型
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            date = date.plusDays(1);

            //查询当天的营业额

            //TODO 这里可以将传入的参数放入一个HashMap中
            Map<Object, Object> map = new HashMap<>();
            map.put("status", status);
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);

            //Double amount = reportMapper.getByOrderTimeAndSatus(status, beginTime, endTime);
            Double amount = reportMapper.getsumByMap(map);
            //TODO 注意这里如果当天没有营业额,其值为null 需要转为0
            amount = amount == null ? 0.0 : amount;
            turnoverList.add(amount);
        }


        String dateStringlist = StringUtils.join(dateList, ',');
        String turnoverStringlist = StringUtils.join(turnoverList, ',');
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(dateStringlist)
                .turnoverList(turnoverStringlist)
                .build();

        return turnoverReportVO;
    }
}
