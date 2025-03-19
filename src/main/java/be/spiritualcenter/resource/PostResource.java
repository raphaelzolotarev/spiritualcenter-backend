package be.spiritualcenter.resource;

import be.spiritualcenter.domain.Post;
import be.spiritualcenter.exception.APIException;
import be.spiritualcenter.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

@RestController
@RequestMapping(path = "/api/blog")
@RequiredArgsConstructor
public class PostResource {
    private final PostService service;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = service.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable int id) {
        Post post = service.findById(id)
                .orElseThrow(() -> new APIException("Post not found with id: " + id));
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post savedPost = service.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable int id, @RequestBody Post post) {
        if (!service.existsById(id)) {
            throw new APIException("Post not found with id: " + id);
        }
        post.setId(id);
        Post updatedPost = service.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        if (!service.existsById(id)) {
            throw new APIException("Post not found with id: " + id);
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        List<Post> posts = service.findByTitleContaining(keyword);
        return ResponseEntity.ok(posts);
    }
}