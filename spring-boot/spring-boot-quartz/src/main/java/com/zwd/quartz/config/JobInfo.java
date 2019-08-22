package com.zwd.quartz.config;

import lombok.Data;

import java.util.Date;

@Data
public class JobInfo {

    private Date date;

    private String jobId;

    private String groupId;

    private String usercontractid;
}
