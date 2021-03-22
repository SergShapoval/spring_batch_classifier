package com.example.batch.classifier.dao.mapper;

import com.example.batch.classifier.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SALARY = "salary";

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder().id(rs.getInt(COLUMN_ID))
                .name(rs.getString(COLUMN_NAME))
                .salary(rs.getInt(COLUMN_SALARY))
                .build();
    }
}
