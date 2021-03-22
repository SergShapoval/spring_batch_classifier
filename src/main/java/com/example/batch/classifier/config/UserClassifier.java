package com.example.batch.classifier.config;

import com.example.batch.classifier.model.User;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class UserClassifier implements Classifier<User, ItemWriter<? super User>> {
    private static final long serialVersionUID = 1L;

    private ItemWriter<User> xmlItemWriter;
    private ItemWriter<User> jsonItemWriter;

    public UserClassifier(ItemWriter<User> xmlItemWriter, ItemWriter<User> jsonItemWriter) {
        this.xmlItemWriter = xmlItemWriter;
        this.jsonItemWriter = jsonItemWriter;
    }

    @Override
    public ItemWriter<? super User> classify(User user) {
        return user.getId() % 2 == 0 ? xmlItemWriter : jsonItemWriter;
    }
}
