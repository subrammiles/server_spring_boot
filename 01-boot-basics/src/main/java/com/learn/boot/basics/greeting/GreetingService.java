package com.learn.boot.basics.greeting;

/** A tiny "library" class. Our own auto-configuration (below) creates it, the way Boot creates DataSources, ObjectMappers... */
public class GreetingService {
    private final String prefix;

    public GreetingService(String prefix) {
        this.prefix = prefix;
    }

    public String greet(String name) {
        return prefix + ", " + name + "!";
    }
}
