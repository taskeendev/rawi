package com.taskeendev.rawi.api.content;

import com.taskeendev.rawi.domain.content.ContentItem;

import java.time.OffsetDateTime;

public record ReadingResponse(
        Long id,
        String url,
        String title,
        String category,
        String summary,
        String imageUrl,
        String source,
        OffsetDateTime createdAt
) {
    public static ReadingResponse from(ContentItem item) {
        return new ReadingResponse(
                item.getId(),
                item.getUrl(),
                item.getTitle(),
                item.getCategory(),
                item.getSummary(),
                item.getImageUrl(),
                item.getSource(),
                item.getCreatedAt()
        );
    }
}
