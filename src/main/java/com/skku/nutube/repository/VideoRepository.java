package com.skku.nutube.repository;

import com.skku.nutube.dto.VideoDto;
import com.skku.nutube.dto.VideoLikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("videoRepository")
public class VideoRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Integer> selectItemId() {
        if(jdbcTemplate == null) {
            System.out.println("jdbcTemplate is null");
        }
        return jdbcTemplate.query("select id from videos", (rs, rowNum) ->
                new Integer(
                        rs.getInt("id")
                ));
    }

    public String selectTitleByItemId(Integer videoId) {
        return jdbcTemplate.queryForObject("select title from videos where id = ?", new Object[]{videoId}, String.class);
    }

    public List<VideoDto> selectVideo() {
        return jdbcTemplate.query("select * from videos", new VideoDtoMapper());
    }

    public class VideoDtoMapper implements RowMapper<VideoDto> {
        // interface method
        public VideoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            VideoDto videoDto = new VideoDto();
            videoDto.setVideoId(rs.getInt("id"));
            videoDto.setVideoName(rs.getString("title"));
            return videoDto;
        }
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
