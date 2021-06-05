package ru.sberbank.holidayunstableservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test/{param}")
    public String test(@PathVariable("param") String param) {
        return "1 " + param;
    }
}
