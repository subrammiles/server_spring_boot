package com.learn.boot.clients.posts;

/** Mirrors the remote JSON. Unknown JSON fields are ignored. */
public record Post(Long id, long userId, String title, String body) {
}
