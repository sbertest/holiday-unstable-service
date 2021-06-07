package ru.sberbank.holidayunstableservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.holidayunstableservice.CustomHealthIndicator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class HolidayRestController {
    private static final Logger log = LoggerFactory.getLogger(HolidayRestController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final int SLEEP_TIME_BOUND = 1000;
    private static final int FAIL_CHANCE = 20;
    private static final int HOLIDAY_CHANCE = 5;
    private static final Health HEALTH_UP = Health.up().build();

    @Autowired
    private CustomHealthIndicator customHealthIndicator;
    private final Random random = new Random();

    private void emulateLongWork() {
        if (!HEALTH_UP.equals(customHealthIndicator.health())) {
            log.info("Health check is not up");
            throw new RuntimeException("fail");
        }
        int sleepTime = random.nextInt(SLEEP_TIME_BOUND);
        log.info("Random sleep time = {}", sleepTime);

        try {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (random.nextInt(100) <= FAIL_CHANCE) {
            log.info("We are fail...");
            throw new RuntimeException("fail");
        }
    }

    @GetMapping("/isHoliday/{checkDate}")
    public String isHoliday(@PathVariable("checkDate") String checkDate) {
        LocalDate date = LocalDate.parse(checkDate, FORMATTER);
        emulateLongWork();
        if (DayOfWeek.SATURDAY.equals(date.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(date.getDayOfWeek())) {
            return "1";
        }
        if (random.nextInt(100) <= HOLIDAY_CHANCE) {
            return "1";
        }
        return "0";
    }
}
