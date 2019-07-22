package com.si.upstream.core.schedule;

import com.si.upstream.core.task.Floor3Task;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author sunxibin
 */
@Component
public class Floor3Load implements CommandLineRunner {
    @Resource
    private Floor3Task floor3Task;

    @Override
    public void run(String... args) throws Exception {
        try {
            floor3Task.startTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
