package com.skku.nutube.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VideoUserRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Integer> selectUserId() {
        return jdbcTemplate.query("select id from users", (rs, rowNum) ->
                new Integer(
                        rs.getInt("id")
                ));
    }
}
