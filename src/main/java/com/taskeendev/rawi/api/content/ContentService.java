package com.taskeendev.rawi.api.content;

import com.taskeendev.rawi.domain.content.ContentItem;
import com.taskeendev.rawi.domain.content.ContentItemRepository;
import com.taskeendev.rawi.domain.content.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentItemRepository repository;

    public ContentItem submit(ContentRequest request) {
        if (repository.existsByUrl(request.url())) {
            throw new DuplicateContentException("URL already submitted: " + request.url());
        }

        ContentItem item = ContentItem.builder()
                .url(request.url())
                .source(request.source())
                .status(ContentStatus.PENDING)
                .build();

        return repository.save(item);
    }
}
