package com.si.upstream.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sunxibin
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "agvRequestThreadPool")
    public ExecutorService createThreadPool() {
        return Executors.newFixedThreadPool(20);
    }
}
