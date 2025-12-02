package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Report;
import com.example.CMCmp3.entity.Report.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r " +
           "LEFT JOIN FETCH r.reporter " +
           "LEFT JOIN FETCH r.reportedSong s " +
           "LEFT JOIN FETCH s.artists a " + // Fetch song's artists
           "LEFT JOIN FETCH r.reportedPlaylist pl " +
           "LEFT JOIN FETCH pl.owner plo " + // Fetch playlist's owner
           "LEFT JOIN FETCH r.reportedSongComment sc " +
           "LEFT JOIN FETCH sc.user scu " + // Fetch song comment's user
           "LEFT JOIN FETCH r.reportedPlaylistComment plc " +
           "LEFT JOIN FETCH plc.user plcu " + // Fetch playlist comment's user
           "WHERE r.status = :status")
    List<Report> findAllByStatus(ReportStatus status);
}
