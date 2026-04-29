package com.taskeendev.rawi.batch;

import com.taskeendev.rawi.domain.content.ContentItem;
import com.taskeendev.rawi.domain.content.ContentItemRepository;
import com.taskeendev.rawi.domain.content.ContentStatus;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
public class BatchConfig {

    @Bean
    public JpaCursorItemReader<ContentItem> pendingContentReader(EntityManagerFactory emf) {
        return new JpaCursorItemReaderBuilder<ContentItem>()
                .name("pendingContentReader")
                .entityManagerFactory(emf)
                .queryString("SELECT c FROM ContentItem c WHERE c.status = :status ORDER BY c.id ASC")
                .parameterValues(Map.of("status", ContentStatus.PENDING))
                .build();
    }

    @Bean
    public RepositoryItemWriter<ContentItem> contentItemWriter(ContentItemRepository repository) {
        return new RepositoryItemWriterBuilder<ContentItem>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step processContentStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   JpaCursorItemReader<ContentItem> pendingContentReader,
                                   ContentProcessor processor,
                                   RepositoryItemWriter<ContentItem> contentItemWriter) {
        return new StepBuilder("processContentStep", jobRepository)
                .<ContentItem, ContentItem>chunk(5, transactionManager)
                .reader(pendingContentReader)
                .processor(processor)
                .writer(contentItemWriter)
                .build();
    }

    @Bean
    public Job contentPipelineJob(JobRepository jobRepository, Step processContentStep) {
        return new JobBuilder("contentPipelineJob", jobRepository)
                .start(processContentStep)
                .build();
    }
}
