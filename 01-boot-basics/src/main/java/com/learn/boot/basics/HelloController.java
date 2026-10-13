package com.learn.boot.basics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.boot.basics.greeting.GreetingService;

/** No DispatcherServlet setup, no Tomcat code, no JSON config: the webmvc starter auto-configures all of it. */
@RestController
public class HelloController {
    private final GreetingService greetings;

    public HelloController(GreetingService greetings) {
        this.greetings = greetings;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "world") String name) {
        return greetings.greet(name);
    }
}
