package com.learn.boot.clients;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.learn.boot.clients.authors.AuthorProfile;
import com.learn.boot.clients.authors.AuthorService;
import com.learn.boot.clients.posts.Post;
import com.learn.boot.clients.posts.PostsClient;
import com.learn.boot.clients.users.UserNotFoundException;

@RestController
public class ApiController {
    private final PostsClient posts;
    private final AuthorService authors;

    public ApiController(PostsClient posts, AuthorService authors) {
        this.posts = posts;
        this.authors = authors;
    }

    @GetMapping("/api/posts/{id}")
    public Post post(@PathVariable long id) {
        return posts.get(id);
    }

    @PostMapping("/api/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return posts.create(post);
    }

    @GetMapping("/api/authors/{id}")
    public AuthorProfile author(@PathVariable long id) {
        return authors.profile(id);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail notFound(UserNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** The upstream failed even after retries: 502 Bad Gateway (it's not OUR bug, it's theirs). */
    @ExceptionHandler(RestClientException.class)
    public ProblemDetail upstreamFailed(RestClientException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "An upstream service failed");
    }
}
