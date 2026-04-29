package com.taskeendev.rawi.api.content;

import com.taskeendev.rawi.domain.content.ContentItem;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService service;

    @PostMapping
    public ResponseEntity<ContentItem> submit(@Valid @RequestBody ContentRequest request) {
        ContentItem item = service.submit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Rawi is running");
    }
}
