package com.learn.boot.clients.authors;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.learn.boot.clients.posts.Post;
import com.learn.boot.clients.posts.PostsClient;
import com.learn.boot.clients.users.User;
import com.learn.boot.clients.users.UsersGateway;

/** Aggregates two remote calls into one response for our own clients (the "backend for frontend" idea). */
@Service
public class AuthorService {
    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final UsersGateway users;
    private final PostsClient posts;

    public AuthorService(UsersGateway users, PostsClient posts) {
        this.users = users;
        this.posts = posts;
    }

    public AuthorProfile profile(long userId) {
        User user = users.findUser(userId);   // essential: if this fails, the request fails
        try {
            List<String> titles = posts.byUser(userId).stream().map(Post::title).toList();
            return new AuthorProfile(user.id(), user.name(), user.email(), titles, false);
        } catch (RestClientException e) {
            // GRACEFUL DEGRADATION: posts are optional, so answer with what we have instead of failing.
            log.warn("Posts service failed for user {}: {}", userId, e.getMessage());
            return new AuthorProfile(user.id(), user.name(), user.email(), List.of(), true);
        }
    }
}
