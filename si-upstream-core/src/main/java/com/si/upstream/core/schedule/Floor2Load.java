package com.si.upstream.core.schedule;

import com.si.upstream.core.task.Floor2Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class Floor2Load implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Floor2Load.class);

    @Resource
    private Floor2Task floor2Task;

    @Override
    public void run(String... args) {
        try {
            floor2Task.startTask();
        } catch (Exception e) {
            LOGGER.warn("Floor2Load start floor2 task throws exception: " + e.getLocalizedMessage(), e);
        }
    }
}
