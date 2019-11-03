package com.skku.nutube.repository;

import com.skku.nutube.dto.VideoLikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class VideoLikeRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    static final String SQL = "select * from likes where userId = ?";

    public List<VideoLikeDto> selectLikesByUserId(int userId) {
        List<VideoLikeDto> videoLikeDtoList = jdbcTemplate.query(SQL, new Object[]{userId}, new VideoLikeDtoMapper());
        return videoLikeDtoList;
    }

    public class VideoLikeDtoMapper implements RowMapper<VideoLikeDto> {
        // interface method
        public VideoLikeDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            VideoLikeDto videoLikeDto = new VideoLikeDto();
            videoLikeDto.setUserId(rs.getInt("userId"));
            videoLikeDto.setVideoId(rs.getInt("videoId"));
            videoLikeDto.setLike(rs.getInt("like"));
            return videoLikeDto;
        }
    }

    public int insertLike(Integer userId, Integer videoId) {
        return jdbcTemplate.update(
                "INSERT INTO likes VALUES (?, ?, 1)",userId, videoId);
    }
}
