package com.taskeendev.rawi.obsidian;

import com.taskeendev.rawi.domain.content.ContentItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class ObsidianSyncService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${rawi.obsidian.vault-path}")
    private String vaultPath;

    public void sync(ContentItem item) {
        try {
            Path vault = Paths.get(vaultPath);
            Path categoryDir = vault.resolve(sanitize(item.getCategory() != null ? item.getCategory() : "Uncategorized"));
            Files.createDirectories(categoryDir);

            String filename = sanitize(item.getTitle() != null ? item.getTitle() : "untitled-" + item.getId()) + ".md";
            Path file = categoryDir.resolve(filename);

            String date = item.getCreatedAt().format(DATE_FMT);
            String content = """
                    ---
                    title: "%s"
                    category: %s
                    source: %s
                    url: %s
                    date: %s
                    tags: [rawi, %s]
                    ---

                    # %s

                    %s

                    ---
                    Source: [%s](%s)
                    """.formatted(
                    item.getTitle(),
                    item.getCategory(),
                    item.getSource() != null ? item.getSource() : "",
                    item.getUrl(),
                    date,
                    item.getCategory() != null ? item.getCategory().toLowerCase() : "uncategorized",
                    item.getTitle(),
                    item.getSummary(),
                    item.getUrl(), item.getUrl()
            );

            Files.writeString(file, content);
            log.info("Obsidian sync: {}", file);

        } catch (IOException e) {
            log.error("Obsidian sync failed for {}: {}", item.getUrl(), e.getMessage());
        }
    }

    private String sanitize(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "-").trim();
    }
}
