package org.example.controller;

import org.example.model.Comment;
import org.example.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private WebClient webClient;

    @GetMapping(value = {"/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Post getPost(@PathVariable("id") int id) {
        return webClient.get()
                .uri(x -> x
                        .path("/posts/{id}")
                        .build(id))
                .retrieve()
                .bodyToMono(Post.class)
                .block();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Post> getPosts() {
        return webClient.get()
                .uri("/posts")
                .retrieve()
                .bodyToFlux(Post.class)
                .collectList()
                .block();
    }

    @GetMapping(value = {"/{id}/comments"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Comment> getComments(@PathVariable("id") int id) {
        return webClient.get()
                .uri(x -> x
                        .path("/posts/{id}/comments")
                        .build(id))
                .retrieve()
                .bodyToFlux(Comment.class)
                .collectList()
                .block();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Post> createPost(@RequestBody Post post) {
        return webClient.post()
                .uri("/posts")
                .bodyValue(post)
                .retrieve()
                .bodyToMono(Post.class);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, value = {"/{id}"})
    public Mono<Post> createPost(@RequestBody Post post, @PathVariable("id") int id) {
        return webClient.put()
                .uri(x -> x
                        .path("/posts/{id}")
                        .build(id))
                .bodyValue(post)
                .retrieve()
                .bodyToMono(Post.class);
    }

    @DeleteMapping(produces = MediaType.TEXT_PLAIN_VALUE, value = {"/{id}"})
    public Mono<String> deletePost(@PathVariable("id") int id) {
        return webClient.delete()
                .uri(x -> x
                        .path("/posts/{id}")
                        .build(id))
                .retrieve()
                .bodyToMono(String.class);
    }

}
