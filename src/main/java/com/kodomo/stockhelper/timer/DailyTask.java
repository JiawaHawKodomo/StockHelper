package com.kodomo.stockhelper.timer;

import com.kodomo.stockhelper.utility.DailyFetchHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 18:38
 **/
@Slf4j
@Component
public class DailyTask {

    private final DailyFetchHelper dailyFetchHelper;

    public DailyTask(DailyFetchHelper dailyFetchHelper) {
        this.dailyFetchHelper = dailyFetchHelper;
    }

    @Scheduled(cron = "0 0 16 * * MON-FRI")
    public void dailyFetch() {
        Date now = new Date();
        log.info("开始爬取并分析每日数据...");
        //爬取每日数据
        dailyFetchHelper.dailyTask(now);
    }
}
