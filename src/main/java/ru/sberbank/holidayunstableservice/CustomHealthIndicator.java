package ru.sberbank.holidayunstableservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class CustomHealthIndicator implements HealthIndicator {
    private static final Logger log = LoggerFactory.getLogger(CustomHealthIndicator.class);
    private static final int UNHEALTHY_CHANCE = 10;
    private static final int PERIOD_DURATION_MIN = 10;
    private static final int PERIOD_DURATION_LENGTH = 10;

    private class UnhealthyEmulation implements Runnable {
        private volatile boolean isRunned = true;
        private volatile boolean isHealthy = true;
        private final Random random = new Random();

        @Override
        public void run() {
            while (isRunned) {
                isHealthy = random.nextInt(100) > UNHEALTHY_CHANCE;
                log.info("Service is healthy = {}", isHealthy);
                int duration = PERIOD_DURATION_MIN + random.nextInt(PERIOD_DURATION_LENGTH);
                log.info("Sleep duration = {}", duration);
                try {
                    TimeUnit.SECONDS.sleep(duration);
                } catch (InterruptedException e) {
                    log.error("Error while sleeping", e);
                    isRunned = false;
                }
            }
            log.info("Unhealthy emulation is stopped");
        }

        public void stop() {
            isRunned = false;
        }

        public boolean isHealthy() {
            return isHealthy;
        }
    }

    private UnhealthyEmulation unhealthyEmulation = new UnhealthyEmulation();

    @PostConstruct
    public void init() {
        log.info("Starting unhealthy emulation");
        Thread thread = new Thread(unhealthyEmulation);
        thread.start();
    }

    @PreDestroy
    public void destroy() {
        log.info("Stopping unhealthy emulation");
        unhealthyEmulation.stop();
    }

    @Override
    public Health health() {
        Health.Builder healthBuilder = unhealthyEmulation.isHealthy() ? Health.up() : Health.down();
        return healthBuilder.build();
    }
}
