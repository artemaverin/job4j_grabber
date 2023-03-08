package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try (Connection connection = init()) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("conection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            int param = Integer.parseInt(propReader().getProperty("rabbit.interval"));
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(param)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(connection);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static Connection init() throws ClassNotFoundException, SQLException {
        Class.forName(propReader().getProperty("driver-class-name"));
        String url = propReader().getProperty("url");
        String login = propReader().getProperty("username");
        String password = propReader().getProperty("password");
        Connection connect = DriverManager.getConnection(url, login, password);
        return connect;
    }

    public static Properties propReader() {
        Properties cfg = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfg;

    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {

            System.out.println("Rabbit runs here ...");
            Connection conection = (Connection) context.getJobDetail().getJobDataMap().get("conection");
            try (PreparedStatement preparedStatement = init().prepareStatement(
                    "insert into rabbit(created_date) values (?);",
                    Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                preparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
