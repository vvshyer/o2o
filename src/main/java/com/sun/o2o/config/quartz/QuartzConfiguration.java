package com.sun.o2o.config.quartz;

import com.sun.o2o.service.ProductSellDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfiguration {
    @Autowired
    private ProductSellDailyService productSellDailyService;
    @Autowired
    private MethodInvokingJobDetailFactoryBean jobDetailFactory;
    @Autowired
    private CronTriggerFactoryBean productSellDailyTriggerFactory;

    @Bean(name = "jobDetailFactory")
    public MethodInvokingJobDetailFactoryBean createJobDetail() {
        //new出JobDetailFactory对象，此工厂主要用来制作一个jobDetail
        //由于我们所做的定时任务根本上讲其实就是执行一个方法 所以用这个工厂比较方便
        MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
        jobDetailFactoryBean.setName("product_sell_daily_job");
        jobDetailFactoryBean.setGroup("job_product_sell_daily_group");
        //对于相同的JobDetail，当指定多个Trigger时，很可能第一个完成前，第二个就开始了
        //指定concurrent设为false，多个job不会并发进行
        jobDetailFactoryBean.setConcurrent(false);
        //指定运行任务的类
        jobDetailFactoryBean.setTargetObject(productSellDailyService);
        //指定运行任务的方法
        jobDetailFactoryBean.setTargetMethod("dailyCalculate");
        return jobDetailFactoryBean;
    }

    @Bean("productSellDailyTriggerFactory")
    public CronTriggerFactoryBean createProductSellDailyTrigger() {
        //创建triggerFactory实例，用来创建trigger
        CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
        triggerFactoryBean.setName("product_sell_daily_trigger");
        triggerFactoryBean.setGroup("job_product_sell_daily_group");
        triggerFactoryBean.setJobDetail(jobDetailFactory.getObject());
        triggerFactoryBean.setCronExpression("0 0 0 * * ? *");
        return triggerFactoryBean;
    }

    @Bean("schedulerFactory")
    public SchedulerFactoryBean createSchedulerFactory(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(productSellDailyTriggerFactory.getObject());
        return schedulerFactoryBean;
    }
}
