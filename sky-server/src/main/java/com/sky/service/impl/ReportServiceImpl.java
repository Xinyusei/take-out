package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

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

            //Double amount = reportMapper.getAmountByOrderTimeAndSatus(status, beginTime, endTime);
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

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        ArrayList<LocalDate> dateList = new ArrayList<>();
        LocalDate start = begin;

        ArrayList<Integer> addUserlist = new ArrayList<>();
        ArrayList<Integer> totalUserlist = new ArrayList<>();
        while (!start.equals(end)) {
            LocalDateTime beginTime = LocalDateTime.of(start, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(start, LocalTime.MAX);

            //TODO 这样做麻烦了
            /*
            List<Integer> userIdList = reportMapper.getByOrderTime(beginTime, endTime);
            Integer addUser = getAddUser(userIdList);
            addUserlist.add(addUser);
            totalUserlist.add(userIdList.size());

            */
            Integer newUser = reportMapper.getUserByTime(beginTime, endTime);
            Integer totalUser = reportMapper.getUserByTime(null, endTime);

            addUserlist.add(newUser);
            totalUserlist.add(totalUser);
            dateList.add(start);
            start = start.plusDays(1);

        }
        String dateStringlist = StringUtils.join(dateList, ',');
        String addUserStringlist = StringUtils.join(addUserlist, ',');
        String toatalUserStringlist = StringUtils.join(totalUserlist, ',');
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(dateStringlist)
                .newUserList(addUserStringlist)
                .totalUserList(toatalUserStringlist)
                .build();
        return userReportVO;
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {

        //订单时期列表
        LocalDate start = begin;
        ArrayList<LocalDate> dateList = new ArrayList<>();
        ArrayList<Integer> validOrderList = new ArrayList<>();
        ArrayList<Integer> totalOrderList = new ArrayList<>();
        Integer completedStatus = Orders.COMPLETED;
        Integer completedOrders = 0, totalOrders = 0;
        while (!start.equals(end)) {
            LocalDateTime beginTime = LocalDateTime.of(start, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(start, LocalTime.MAX);
            //select count(*) from orders where status = ? and order_time < ? and order_time > ?
            Integer DailycompletedOrders = reportMapper.getNumByOrderTimeAndSatus(completedStatus, beginTime, endTime);
            Integer DailytotalOrders = reportMapper.getNumByOrderTimeAndSatus(null, beginTime, endTime);


            completedOrders += DailycompletedOrders;
            totalOrders += DailytotalOrders;

            dateList.add(start);
            validOrderList.add(DailycompletedOrders);
            totalOrderList.add(DailytotalOrders);
            start = start.plusDays(1);
        }


        //构建返回订单对象
        String resDateList = StringUtils.join(dateList, ',');
        String resValidOrderList = StringUtils.join(validOrderList, ',');
        String resTotalOrderList = StringUtils.join(totalOrderList, ',');
        Double orderCompletionRate = 0.0;
        if (totalOrders != 0) {
            orderCompletionRate = 1.0 * completedOrders / totalOrders;
        }
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(resDateList)
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(resTotalOrderList)
                .totalOrderCount(totalOrders)
                .validOrderCount(completedOrders)
                .validOrderCountList(resValidOrderList)
                .build();

        return orderReportVO;
    }

    /**
     * 查询销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getsalesTop10Report(LocalDate begin, LocalDate end) {

        //TODO 这个SQL很巧妙
        List<GoodsSalesDTO> goodsSalesDTOList = reportMapper.getTop10Report(begin, end);


        ArrayList<Integer> numberlist = new ArrayList<>();
        ArrayList<String> namelist = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOList) {
            namelist.add(goodsSalesDTO.getName());
            numberlist.add(goodsSalesDTO.getNumber());

        }
        String resNamelist = StringUtils.join(namelist, ',');
        String resNumberlist = StringUtils.join(numberlist, ',');
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(resNamelist)
                .numberList(resNumberlist)
                .build();

        return salesTop10ReportVO;
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        //查询概览运营数据，提供给Excel模板文件
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Integer getAddUser(List<Integer> userIdList) {
        Integer addUser = 0;
        HashSet<Integer> userSet = new HashSet<>();
        for (Integer userId : userIdList) {
            //新增用户
            if (!userSet.contains(userId)) {
                addUser++;
            } else {
                userSet.add(userId);
            }
        }
        return addUser;
    }
}
