package com.learn.boot.rest.basics;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Controller (without "Rest") returns VIEW NAMES (for server-side HTML like Thymeleaf).
 * Adding @ResponseBody makes a method write its return value to the body instead.
 * @RestController = @Controller + @ResponseBody on every method.
 */
@Controller
public class LegacyController {

    @GetMapping(value = "/basics/legacy", produces = "text/plain")
    @ResponseBody
    public String hello() {
        return "Hello from a @Controller + @ResponseBody method";
    }
}
