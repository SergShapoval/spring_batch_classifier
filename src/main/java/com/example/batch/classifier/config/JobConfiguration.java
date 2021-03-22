package com.example.batch.classifier.config;

import com.example.batch.classifier.aggregator.CustomLineAggregator;
import com.example.batch.classifier.dao.mapper.UserRowMapper;
import com.example.batch.classifier.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JobConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobConfiguration.class);

    private static final String OUT_PUT_FILE_NAME = "UserOutput";
    private static final String OUT_PUT_FILE_EXTENSION = ".out";
    private static final String STEP_NAME = "Step_1";
    private static final String JOB_NAME = "Job";

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private DataSource dataSource;

    public JobConfiguration(JobBuilderFactory jobBuilderFactory,
                            StepBuilderFactory stepBuilderFactory,
                            DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcPagingItemReader<User> UserPagingItemReader() {
        JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(1000);
        reader.setRowMapper(new UserRowMapper());

        Map<String, Order> sortKeys = Collections.singletonMap("id", Order.ASCENDING);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, name, salary");
        queryProvider.setFromClause("FROM User");
        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);
        return reader;
    }

    @Bean
    public FlatFileItemWriter<User> jsonItemWriter() throws Exception {

        String userOutputPath = File.createTempFile(OUT_PUT_FILE_NAME, OUT_PUT_FILE_EXTENSION).getAbsolutePath();
        LOGGER.info("Output path -> {}", userOutputPath);
        FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
        writer.setLineAggregator(new CustomLineAggregator());
        writer.setResource(new FileSystemResource(userOutputPath));
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public StaxEventItemWriter<User> xmlItemWriter() throws Exception {

        String userOutputPath = File.createTempFile(OUT_PUT_FILE_NAME, OUT_PUT_FILE_EXTENSION).getAbsolutePath();
        LOGGER.info("Output path -> {}", userOutputPath);
        Map<String, Class> aliases = new HashMap<>();
        aliases.put("User", User.class);
        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(aliases);

        StaxEventItemWriter<User> writer = new StaxEventItemWriter<>();
        writer.setRootTagName("Users");
        writer.setMarshaller(marshaller);
        writer.setResource(new FileSystemResource(userOutputPath));
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public ClassifierCompositeItemWriter<User> classifierUserCompositeItemWriter() throws Exception {
        ClassifierCompositeItemWriter<User> compositeItemWriter = new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(new UserClassifier(xmlItemWriter(), jsonItemWriter()));
        return compositeItemWriter;
    }

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get(STEP_NAME)
                .<User, User>chunk(10)
                .reader(UserPagingItemReader())
                .writer(classifierUserCompositeItemWriter())
                .stream(xmlItemWriter())
                .stream(jsonItemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step1())
                .build();
    }

}
