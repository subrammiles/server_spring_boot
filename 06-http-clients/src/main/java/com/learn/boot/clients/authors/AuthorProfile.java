package com.learn.boot.clients.authors;

import java.util.List;

/** degraded = true when part of the data could not be fetched and was left out. */
public record AuthorProfile(long id, String name, String email, List<String> postTitles, boolean degraded) {
}
