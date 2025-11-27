package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.SongListenLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SongListenLogRepository extends JpaRepository<SongListenLog, Long> {

    /**
     * Đếm số lượt nghe của các bài hát có tag nhất định trong một khoảng thời gian.
     * @param startTime Thời gian bắt đầu
     * @param endTime Thời gian kết thúc
     * @param tagName Tên của tag (ví dụ: "V-Pop")
     * @return Tổng số lượt nghe
     */
    @Query("SELECT COUNT(sll.id) " +
           "FROM SongListenLog sll " +
           "JOIN sll.song s " +
           "JOIN s.tags t " +
           "WHERE sll.listenTimestamp BETWEEN :startTime AND :endTime AND t.name = :tagName")
    long countListensBetweenWithTag(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("tagName") String tagName
    );

    /**
     * Đếm số lượt nghe của một bài hát cụ thể trong một khoảng thời gian.
     * @param songId ID của bài hát
     * @param startTime Thời gian bắt đầu
     * @param endTime Thời gian kết thúc
     * @return Tổng số lượt nghe của bài hát đó trong khoảng thời gian
     */
    @Query("SELECT COUNT(sll.id) " +
           "FROM SongListenLog sll " +
           "WHERE sll.song.id = :songId AND sll.listenTimestamp BETWEEN :startTime AND :endTime")
    long countListensForSongBetween(
            @Param("songId") Long songId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
