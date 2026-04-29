package com.taskeendev.rawi.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentItemRepository extends JpaRepository<ContentItem, Long> {

    List<ContentItem> findByStatus(ContentStatus status);

    List<ContentItem> findByStatusOrderByCreatedAtDesc(ContentStatus status);

    List<ContentItem> findByStatusAndCategoryOrderByCreatedAtDesc(ContentStatus status, String category);

    List<ContentItem> findByCategory(String category);

    boolean existsByUrl(String url);
}
