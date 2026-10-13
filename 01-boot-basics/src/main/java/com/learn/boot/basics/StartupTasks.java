package com.learn.boot.basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Code to run once at startup: ApplicationRunner (parsed args) or CommandLineRunner (raw String[] args).
 * Typical uses: seed data, warm caches, print useful URLs.
 */
@Component
public class StartupTasks implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupTasks.class);

    private final Environment env;

    public StartupTasks(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        // e.g. mvn spring-boot:run -Dspring-boot.run.arguments="--mode=demo extra"
        log.info("ApplicationRunner: option args = {}, non-option args = {}",
                args.getOptionNames(), args.getNonOptionArgs());
    }

    /** Fired after everything (runners included) is done: the app is ready for traffic. */
    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        log.info("Ready! Try http://localhost:{}/hello?name=Boot", env.getProperty("local.server.port"));
    }
}
