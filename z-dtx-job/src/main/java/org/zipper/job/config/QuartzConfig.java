package org.zipper.job.config;

import lombok.SneakyThrows;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.zipper.helper.quartz.QuartzUtils;
import org.zipper.job.service.rpc.TransportService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TransportService transportService;

    @SneakyThrows
    @PostConstruct
    public void init() {
        scheduler.getContext().putIfAbsent("transportService",transportService);
        QuartzUtils.init(scheduler);
    }

    @SneakyThrows
    @Bean
    public SchedulerFactoryBean getSchedulerFactoryBean() {

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setQuartzProperties(properties());
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactoryBean.setDataSource(dataSource);
        JobDataMap dataMap = new JobDataMap();
        schedulerFactoryBean.setSchedulerContextAsMap(dataMap);
        return schedulerFactoryBean;
    }

    @Bean
    public Properties properties() throws IOException {
        Properties prop = new Properties();
        prop.load(new ClassPathResource("/quartz.properties").getInputStream());
        return prop;
    }
}
