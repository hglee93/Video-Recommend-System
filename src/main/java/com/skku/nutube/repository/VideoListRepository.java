package com.skku.nutube.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class VideoListRepository {

    JdbcTemplate jdbcTemplate;

    private static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/nutube?autoReconnect=true&useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC";
    private static final String dbUsername = "root";
    private static final String dbPassword = "1234";

    public VideoListRepository() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);

        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
    }

    public List<Integer> selectItemId() {
        if(jdbcTemplate == null) {
            System.out.println("jdbcTemplate is null");
        }
        return jdbcTemplate.query("select id from videos", (rs, rowNum) ->
                new Integer(
                        rs.getInt("id")
                ));
    }

    public List<String> selectTagListByItemId(Integer videoId) {

        String SQL = "select tag from video_tag where videoId = " + videoId;
        List<String> result = jdbcTemplate.query(SQL
                ,new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("tag").replaceAll("\r", "");
                    }
                });

        return result;
    }
}
