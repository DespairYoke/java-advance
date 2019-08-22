package com.zwd.quartz.controller;

import com.zwd.quartz.config.JobInfo;
import com.zwd.quartz.config.QuartzManager;
import org.quartz.DateBuilder;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author zhuweidong
 */
@RestController
public class TestJobController {

    @Autowired
    private QuartzManager quartzManager;
    @RequestMapping(value = "/")
    public void index(){


        JobInfo jobInfo = new JobInfo();
        Date date = DateBuilder.futureDate(1, DateBuilder.IntervalUnit.MINUTE);
        jobInfo.setDate(date);
        jobInfo.setJobId("cccjobid"+System.currentTimeMillis());
        jobInfo.setGroupId("groupid"+System.currentTimeMillis());
        jobInfo.setUsercontractid("1211111");

        try {
            System.out.println("发布任务" + jobInfo.getUsercontractid());
            quartzManager.startJob(jobInfo);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
