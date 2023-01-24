/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.core.work.Reset
 *  java.lang.Object
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.scheduling.annotation.EnableScheduling
 *  org.springframework.scheduling.annotation.Scheduled
 */
package com.hypergryph.arknights.core.work;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class Reset {
    private static final Logger LOGGER = LogManager.getLogger();

    @Scheduled(cron="0 0 4 * * ?")
    private void CheckIn() {
    }

    @Scheduled(cron="0 0 4 * * ?")
    private void PracticeTicket() {
    }
}

