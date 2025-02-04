package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;


    /**
     * 统计指定时间区间内的营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        // 当前集合用于将从begin开始到end结束中的所有日期都收集
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            // 查询date日期对应的营业额数据，营业额：状态已完成的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);

        }

        // 封装返回结果
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内用户数据
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于将从begin开始到end结束中的所有日期都收集
        List<LocalDate> dateList = new ArrayList<>();

        // 查询开始日期前的用户数量
        LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.MIN);
        Map map = new HashMap();
        map.put("end", endTime);
        Integer userTotal = userMapper.countByMap(map);
        userTotal = userTotal == null ? 0 : userTotal;

        dateList.add(begin);
        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 用户累加量和用户新增
        List<Integer> userTotalList = new ArrayList<>();
        List<Integer> userDailyList = new ArrayList<>();



        for (LocalDate date : dateList) {
            // 查询date日期对应的用户数据
            LocalDateTime beginDaily = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDaily = LocalDateTime.of(date, LocalTime.MAX);

            Map map_daily = new HashMap();
            map_daily.put("begin", beginDaily);
            map_daily.put("end", endDaily);
            Integer new_user = userMapper.countByMap(map_daily);
            new_user = new_user == null ? 0 : new_user;
            userDailyList.add(new_user);
            userTotal += new_user;
            userTotalList.add(userTotal);
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(userDailyList, ","))
                .totalUserList(StringUtils.join(userTotalList, ","))
                .build();
    }
}
