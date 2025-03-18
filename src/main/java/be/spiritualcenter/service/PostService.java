package be.spiritualcenter.service;


import be.spiritualcenter.domain.Post;
import be.spiritualcenter.repository.PostRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepo repo;

    public List<Post> findAll() {
        return repo.findAll();
    }

    public Optional<Post> findById(int id) {
        return repo.findById(id);
    }

    public Post save(Post post) {
        if (post.getDate() == null) {post.setDate(LocalDateTime.now());}
        return repo.save(post);
    }

    public boolean existsById(int id) {
        return repo.existsById(id);
    }

    public void deleteById(int id) {
        repo.deleteById(id);
    }

    public List<Post> findByTitleContaining(String keyword) {
        return repo.findAll().stream().filter(post -> post.getTitle().toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());
    }
}
