package com.taskeendev.rawi.batch;

import com.taskeendev.rawi.ai.AiAnalysis;
import com.taskeendev.rawi.ai.GeminiService;
import com.taskeendev.rawi.domain.content.ContentItem;
import com.taskeendev.rawi.domain.content.ContentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentProcessor implements ItemProcessor<ContentItem, ContentItem> {

    private final GeminiService geminiService;

    private static final int MAX_TEXT_LENGTH = 4000;

    @Override
    public ContentItem process(ContentItem item) {
        log.info("Processing: {}", item.getUrl());
        try {
            Document doc = Jsoup.connect(item.getUrl())
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();

            if (item.getTitle() == null || item.getTitle().isBlank()) {
                item.setTitle(doc.title());
            }

            String text = doc.body().text();
            if (text == null || text.isBlank()) {
                log.warn("Empty body for URL: {} — marking FAILED", item.getUrl());
                item.setStatus(ContentStatus.FAILED);
                return item;
            }
            if (text.length() > MAX_TEXT_LENGTH) {
                text = text.substring(0, MAX_TEXT_LENGTH);
            }

            AiAnalysis analysis = geminiService.analyze(text);
            item.setSummary(analysis.summary());
            item.setCategory(analysis.category());
            item.setStatus(ContentStatus.DONE);

        } catch (Exception e) {
            log.error("Failed to process {}: {}", item.getUrl(), e.getMessage());
            item.setStatus(ContentStatus.FAILED);
        }
        return item;
    }
}
