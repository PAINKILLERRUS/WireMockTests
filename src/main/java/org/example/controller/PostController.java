package org.example.controller;

import org.example.model.Comment;
import org.example.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

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
    public List<Comment> getComments(@PathVariable("id") int id){
        return webClient.get()
                .uri(x->x
                        .path("/posts/{id}/comments")
                        .build(id))
                .retrieve()
                .bodyToFlux(Comment.class)
                .collectList()
                .block();
    }
}
