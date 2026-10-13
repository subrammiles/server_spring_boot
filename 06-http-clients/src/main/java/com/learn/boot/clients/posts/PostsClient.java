package com.learn.boot.clients.posts;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * DECLARATIVE HTTP client: describe the remote API as an interface, and Spring generates the
 * implementation (like Spring Data repositories, but for HTTP). Same annotations style as controllers.
 * Registered in ClientsConfig with @ImportHttpServices; base URL and timeouts in application.yml.
 */
@HttpExchange("/posts")
public interface PostsClient {

    @GetExchange("/{id}")
    Post get(@PathVariable long id);

    @GetExchange
    List<Post> byUser(@RequestParam long userId);

    @PostExchange
    Post create(@RequestBody Post post);
}
