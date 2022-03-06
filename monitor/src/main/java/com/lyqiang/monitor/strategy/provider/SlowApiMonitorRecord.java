package com.lyqiang.monitor.strategy.provider;

import com.lyqiang.monitor.common.alarm.WeChatAlarmUtils;
import com.lyqiang.monitor.config.MonitorProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lyqiang
 */
@Component
public class SlowApiMonitorRecord {
    /**
     * 记录慢请求的时间戳
     */
    private static final Map<String, LinkedBlockingQueue<Long>> SLOW_API_DETAIL_MAP = new ConcurrentHashMap<>();

    /**
     * 记录最后发送时间
     */
    private static final Map<String, Long> SLOW_API_OLDEST_TIME_MAP = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final MonitorProperties monitorProperties;

    public SlowApiMonitorRecord(MonitorProperties monitorProperties) {
        this.monitorProperties = monitorProperties;
    }

    public void recordSlowAsync(String url, double costTime, Object param) {
        if (!monitorProperties.isApiCostAlarmByPeriod()) {
            WeChatAlarmUtils.sendApiCostAlarm(url, costTime, param);
        } else {
            CompletableFuture.runAsync(() -> recordSlowUrl(url, costTime, param));
        }
    }

    private void recordSlowUrl(String url, double costTime, Object param) {
        long currentTimeMillis = System.currentTimeMillis();
        if (!SLOW_API_DETAIL_MAP.containsKey(url)) {
            initUrlRecord(url, currentTimeMillis);
        } else {
            SLOW_API_DETAIL_MAP.get(url).add(currentTimeMillis);
        }
        try {
            alarmIfNecessary(url, currentTimeMillis, costTime, param);
        } catch (Exception e) {
            cleanOverTimeRecord(url, currentTimeMillis);
        }
    }

    /**
     * 第一次记录
     */
    private void initUrlRecord(String url, long currentTimeMillis) {
        LinkedBlockingQueue<Long> list = new LinkedBlockingQueue<>();
        list.add(currentTimeMillis);
        SLOW_API_DETAIL_MAP.put(url, list);
        SLOW_API_OLDEST_TIME_MAP.put(url, currentTimeMillis);
    }

    /**
     * 以慢请求记录为触发点，查询近 apiCostAlarmCountPeriod 时间内的超时次数
     * 可能会不精确，例如 30 秒内超过 10 次 需要报警，在前 20 秒超时 11 次, 但在 30 秒时未超时则不会报警
     */
    private void alarmIfNecessary(String url, long currentTimeMillis, double costTime, Object param) throws InterruptedException {
        Long oldestTime = SLOW_API_OLDEST_TIME_MAP.get(url);
        Integer apiCostAlarmCountPeriod = monitorProperties.getApiCostAlarmCountPeriod();
        //距离上一次报警还未超过设置的间隔时间
        if (currentTimeMillis - oldestTime < apiCostAlarmCountPeriod) {
            return;
        }
        boolean acquire = lock.tryLock(100, TimeUnit.MILLISECONDS);
        if (!acquire) {
            return;
        }
        try {
            doAlarmIfNecessary(url, currentTimeMillis, costTime, param);
        } catch (Exception ex) {
            cleanOverTimeRecord(url, currentTimeMillis);
        } finally {
            lock.unlock();
        }
    }

    private void doAlarmIfNecessary(String url, long currentTimeMillis, double costTime, Object param) {
        Integer apiCostAlarmCountPeriod = monitorProperties.getApiCostAlarmCountPeriod();
        // 查看慢请求个数是否超过阈值
        Integer apiCostAlarmCountThreshold = monitorProperties.getApiCostAlarmCountThreshold();
        cleanOverTimeRecord(url, currentTimeMillis);
        int totalCount = SLOW_API_DETAIL_MAP.get(url).size();
        if (totalCount > apiCostAlarmCountThreshold) {
            WeChatAlarmUtils.sendApiCostAlarm(url, apiCostAlarmCountPeriod, totalCount, costTime, param);
            SLOW_API_OLDEST_TIME_MAP.put(url, currentTimeMillis);
        }
    }

    /**
     * 清理过期的慢请求时间戳
     */
    private void cleanOverTimeRecord(String url, long currentTimeMillis) {
        LinkedBlockingQueue<Long> slowList = SLOW_API_DETAIL_MAP.get(url);
        if (CollectionUtils.isEmpty(slowList)) {
            return;
        }
        long overTimeSplit = currentTimeMillis - monitorProperties.getApiCostAlarmCountPeriod();
        Iterator<Long> iterator = slowList.iterator();
        while (iterator.hasNext()) {
            Long time = iterator.next();
            if (time < overTimeSplit) {
                iterator.remove();
            } else {
                break;
            }
        }
        Long oldestEle = slowList.peek();
        Long oldestTime = oldestEle == null ? currentTimeMillis : oldestEle;
        SLOW_API_OLDEST_TIME_MAP.put(url, oldestTime);
    }
}
