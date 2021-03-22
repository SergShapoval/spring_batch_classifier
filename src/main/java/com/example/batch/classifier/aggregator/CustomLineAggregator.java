package com.example.batch.classifier.aggregator;

import com.example.batch.classifier.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.LineAggregator;

public class CustomLineAggregator implements LineAggregator<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLineAggregator.class);

    private static final String EXCEPTION_MESSAGE = "Unable to serialize User";

    @Override
    public String aggregate(User item) {
        try {
            return new ObjectMapper().writeValueAsString(item);
        } catch (Exception e) {
            LOGGER.info(EXCEPTION_MESSAGE);
            throw new RuntimeException(EXCEPTION_MESSAGE, e);
        }
    }
}
