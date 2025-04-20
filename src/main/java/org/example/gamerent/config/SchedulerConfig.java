package org.example.gamerent.config;

import org.example.gamerent.services.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private RentalService rentalService;

    @Scheduled(fixedDelay = 3600000)
    public void declineOld() {
        rentalService.autoDeclinePending();
    }
}