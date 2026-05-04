package com.taskeendev.rawi.api.content;

import com.taskeendev.rawi.ai.AiAnalysis;
import com.taskeendev.rawi.ai.GeminiService;
import com.taskeendev.rawi.domain.content.ContentItem;
import com.taskeendev.rawi.domain.content.ContentItemRepository;
import com.taskeendev.rawi.domain.content.ContentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentItemRepository repository;
    private final GeminiService geminiService;

    public ContentItem submit(ContentRequest request) {
        if (repository.existsByUrl(request.url())) {
            throw new DuplicateContentException("URL already submitted: " + request.url());
        }

        ContentItem item = ContentItem.builder()
                .url(request.url())
                .title(request.title())
                .source(request.source())
                .status(ContentStatus.PENDING)
                .build();

        if (request.content() != null && !request.content().isBlank()) {
            try {
                AiAnalysis analysis = geminiService.analyze(request.content());
                item.setSummary(analysis.summary());
                item.setCategory(analysis.category());
                item.setStatus(ContentStatus.DONE);
                log.info("Processed inline content for: {}", request.url());
            } catch (Exception e) {
                log.error("AI analysis failed for {}: {}", request.url(), e.getMessage());
                item.setStatus(ContentStatus.FAILED);
            }
        }

        return repository.save(item);
    }

    public Page<ReadingResponse> listDone(String category, Pageable pageable) {
        Page<ContentItem> items = (category != null && !category.isBlank())
                ? repository.findByStatusAndCategory(ContentStatus.DONE, category, pageable)
                : repository.findByStatus(ContentStatus.DONE, pageable);
        return items.map(ReadingResponse::from);
    }

    public List<String> listCategories() {
        return repository.findDistinctCategories();
    }

    public Optional<ContentItem> findById(Long id) {
        return repository.findById(id);
    }
}
