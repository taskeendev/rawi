package com.taskeendev.rawi.domain.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContentItemRepository extends JpaRepository<ContentItem, Long> {

    List<ContentItem> findByStatus(ContentStatus status);

    List<ContentItem> findByStatusOrderByCreatedAtDesc(ContentStatus status);

    List<ContentItem> findByStatusAndCategoryOrderByCreatedAtDesc(ContentStatus status, String category);

    Page<ContentItem> findByStatus(ContentStatus status, Pageable pageable);

    Page<ContentItem> findByStatusAndCategory(ContentStatus status, String category, Pageable pageable);

    @Query("SELECT DISTINCT c.category FROM ContentItem c WHERE c.status = 'DONE' AND c.category IS NOT NULL ORDER BY c.category")
    List<String> findDistinctCategories();

    List<ContentItem> findByCategory(String category);

    boolean existsByUrl(String url);
}
