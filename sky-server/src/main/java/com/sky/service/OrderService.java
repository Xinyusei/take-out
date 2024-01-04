package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 查看订单详情
     * @param orderId
     * @return
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 取消订单
     * @param orderId 订单id
     */
    void orderCancel(Long orderId);

    /**
     * 再来一单
     * @param orderId
     */
    void repetitionOrder(Long orderId);

    /**
     * 管理端订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageQuery4Admin(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO getStatistics();

    /**
     * 商家接单
     * @param ordersConfirmDTO
     */
    void orderConfirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     * @return
     */
    void orderRejection(OrdersRejectionDTO ordersRejectionDTO);
    /**
     * 取消订单
     *
     * @return
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);


    /**
     * 派送订单
     *
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);
}
