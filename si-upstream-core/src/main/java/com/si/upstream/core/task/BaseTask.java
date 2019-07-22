package com.si.upstream.core.task;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

/**
 * @author sunxibin
 */
public abstract class BaseTask {

    private static Logger logger = LoggerFactory.getLogger(BaseTask.class);

    /**
     * 多线程定时任务执行. 可以设置执行线程池数（默认一个线程）
     * 1. 使用前必须得先调用initialize()进行初始化
     * 2. schedule(Runnable task, Trigger trigger) 指定一个触发器执行定时任务。可以使用CronTrigger来指定Cron表达式，执行定时任务
     * 3. shutDown()方法，执行完后可以关闭线程
     */
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * @Description: 获取定时任务执行时间规则表达式
     */
    protected abstract String getCron();

    /**
     * @Description: 控制定时器的开关
     */
    protected abstract boolean isOpen();

    /**
     * @Description: 定时任务执行的方法
     */
    protected abstract void executeTask();

    /**
     * @Description: 定时任务开始执行的方法
     */
    public void startTask() {
        // 1. 判断定时器开关
        if (!isOpen()) {
            return;
        }

        // 2. 实例化一个线程池任务调度器
        if (null == threadPoolTaskScheduler) {
            threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        }

        // 3. 初始化threadPoolTaskScheduler
        threadPoolTaskScheduler.initialize();

        // 4. 创建一个任务执行线程
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isOpen()) {
                        stopTask();
                        return;
                    }
                    //执行任务
                    executeTask();
                } catch (Exception e) {
                    logger.error("定时任务执行失败，{}", e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        // 5. 创建一个触发器
        Trigger trigger = new Trigger() {

            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String cron = getCron();
                if (StringUtils.isBlank(cron)) {
                    return null;
                }
                CronTrigger cronTrigger = new CronTrigger(cron);
                return cronTrigger.nextExecutionTime(triggerContext);
            }
        };

        // 6. 执行schedule(Runnable task, Trigger trigger)任务调度方法
        threadPoolTaskScheduler.schedule(runnable, trigger);
    }

    /**
     * @Description: 定时任务终止执行的方法
     */
    public void stopTask() {
        if (null != threadPoolTaskScheduler) {
            //关闭线程
            threadPoolTaskScheduler.shutdown();
        }
    }

}
