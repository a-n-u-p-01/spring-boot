package com.spring_boot.spring_batch.config;

import com.spring_boot.spring_batch.entity.Order;
import com.spring_boot.spring_batch.listener.CustomJobListener;
import com.spring_boot.spring_batch.processor.OrderProcessor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import static com.spring_boot.spring_batch.constant.BatchJobConstant.ORDER_ITEM_READER;
import static com.spring_boot.spring_batch.constant.BatchJobConstant.ORDER_PROCESS_JOB;

@Configuration
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private OrderProcessor processor;

    @Value("${app.csv.fileHeaders}")
    private String[] names;

    @Bean
    public FlatFileItemReader<Order> reader() {
        return new FlatFileItemReaderBuilder<Order>()
                .name(ORDER_ITEM_READER)
                .resource(new ClassPathResource("csv/orders.csv"))
                .linesToSkip(1)
                .delimited()
                .names(names)
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Order>() {{
                    setTargetType(Order.class);
                }})
                .build();
    }


    @Bean
    public LineMapper<Order> lineMapper() {
        final DefaultLineMapper<Order> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(names);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<Order>() {{
            setTargetType(Order.class);
        }});

        return defaultLineMapper;
    }

    @Bean
    public JpaItemWriter<Order> writer(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Order> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Step step(FlatFileItemReader<Order> reader, JpaItemWriter<Order> writer) {
        return new StepBuilder("step", jobRepository)
                .<Order, Order>chunk(5, transactionManager) // Chunk size of 5
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    @Bean
    public Job job(CustomJobListener listener, Step step) {
        return new JobBuilder(ORDER_PROCESS_JOB, jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }
}
