package com.zwd.quartz.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QuartzManager {

    /**
     * 任务调度
     */
    @Autowired
    private Scheduler scheduler;

    /**
     * 开始执行定时任务
     */
    public void startJob(JobInfo jobInfo) throws SchedulerException {
        startJobTask(scheduler,jobInfo);
//        scheduler.start();
    }

    /**
     * 启动定时任务
     * @param scheduler
     */
    private void startJobTask(Scheduler scheduler, JobInfo jobInfo) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("usercontractid",jobInfo.getUsercontractid());
        JobDetail jobDetail= JobBuilder.newJob(TestJob.class)
                .withIdentity(jobInfo.getJobId(),jobInfo.getGroupId())
                .usingJobData(jobDataMap)
                .build();

        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();

        SimpleTrigger simpleTrigger= TriggerBuilder.newTrigger()
                .withIdentity(jobInfo.getJobId(),jobInfo.getGroupId())
                .startAt(jobInfo.getDate())
                .withSchedule(simpleScheduleBuilder.withMisfireHandlingInstructionFireNow())
                .build();
        scheduler.scheduleJob(jobDetail,simpleTrigger);

    }

}