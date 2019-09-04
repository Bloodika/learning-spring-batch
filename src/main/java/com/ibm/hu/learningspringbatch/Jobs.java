package com.ibm.hu.learningspringbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompletionPolicySupport;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import javax.sound.sampled.Line;
import javax.sql.DataSource;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class Jobs {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcPagingItemReader<Map<String, Object>> jdbcReader(){
        JdbcPagingItemReader<Map<String, Object>> jdbcPagingItemReader = new JdbcPagingItemReader<>();
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setRowMapper(new ColumnMapRowMapper());
        jdbcPagingItemReader.setFetchSize(1);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from tickets.history");

        Map<String,Order> sortKeys = new HashMap<String,Order>();
        sortKeys.put("id",Order.ASCENDING);
        sortKeys.put("folder_id",Order.DESCENDING);

        queryProvider.setSortKeys(sortKeys);

        jdbcPagingItemReader.setQueryProvider(queryProvider);

        return jdbcPagingItemReader;
    }

    @Bean
    public ItemWriter fileWriter(){
        FlatFileItemWriter itemWriter = new FlatFileItemWriter();
        itemWriter.setLineAggregator(new DelimitedLineAggregator());
        itemWriter.setResource(new FileSystemResource("C:/Users/Public/Desktop/output.txt"));
        return itemWriter;
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .chunk(1)
                .reader(jdbcReader())
                .processor(mqFileCreator())
                .writer(fileWriter())
                .build();
    }
    @Bean
    public MqFileCreator mqFileCreator() {
        return new MqFileCreator();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("job"+ Calendar.getInstance().getTimeInMillis())
                .start(step1())
                .build();
    }
}
