package com.learn.boot.rest.basics;

/** Request body used by the basics examples (ported from the old RestControler1). */
public record Employee(int id, String name, int salary) {
}
