package com.gautam.batch_processing.config;

import com.gautam.batch_processing.model.Product;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {
    @Bean
    public Job jobBean(JobRepository jobRepository,
                       JobCompletionNotificationImpl listener, Step steps) {
        return new JobBuilder("jobBean", jobRepository)
                .listener(listener)
                .start(steps)
                .build();
    }

    @Bean
    public Step steps(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      ItemReader<Product> itemReader, ItemWriter<Product> itemWriter,
                      ItemProcessor<Product, Product> itemProcessor) {
        return new StepBuilder("jobStep", jobRepository)
                .<Product, Product>chunk(5)
                .transactionManager(transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    // reader
    @Bean
    public FlatFileItemReader<Product> reader() {
        return new FlatFileItemReaderBuilder<Product>()
                .name("itemReader")
                .resource(new ClassPathResource("products.csv"))
                .delimited()
                .names("product_id", "title", "description", "price", "discount")
                .linesToSkip(1)
                .targetType(Product.class)
                .build();
    }

    @Bean
    public ItemProcessor<Product, Product> processor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<Product> itemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
                .sql("INSERT INTO PRODUCTS(product_id,title,description,price,discount,discounted_price) values(:product_id,:title,:description,:price,:discount,:discountedPrice)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

}
