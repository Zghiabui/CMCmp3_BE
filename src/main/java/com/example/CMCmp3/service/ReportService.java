package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.CreateReportDTO;
import com.example.CMCmp3.dto.ReportDTO;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final SongCommentRepository songCommentRepository;
    private final PlaylistCommentRepository playlistCommentRepository;

    // Helper method to get current authenticated user
    private User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Current user not found."));
    }

    // Helper method to convert Report entity to ReportDTO
    public ReportDTO toDTO(Report report) {
        System.out.println("Processing report ID: " + report.getId() + ", Type: " + report.getReportType());

        ReportDTO.ReportDTOBuilder dtoBuilder = ReportDTO.builder()
                .id(report.getId())
                .reason(report.getReason())
                .createdAt(report.getCreatedAt())
                .reportType(report.getReportType())
                .status(report.getStatus());

        if (report.getReporter() != null) {
            dtoBuilder.reporter(new ReportDTO.ReporterDTO(report.getReporter().getId(), report.getReporter().getDisplayName()));
        }

        if (report.getReportedSong() != null) {
            System.out.println("  - Reported Song: " + report.getReportedSong().getTitle());
            dtoBuilder.reportedSong(new ReportDTO.ReportedSongDTO(
                    report.getReportedSong().getId(),
                    report.getReportedSong().getTitle(),
                    report.getReportedSong().getImageUrl(),
                    report.getReportedSong().getArtists() != null && !report.getReportedSong().getArtists().isEmpty() ?
                            report.getReportedSong().getArtists().stream()
                                    .map(artist -> artist.getName())
                                    .collect(Collectors.joining(", ")) : "Unknown Artist"
            ));
        } else {
            System.out.println("  - Reported Song: NULL");
        }

        if (report.getReportedPlaylist() != null) {
            System.out.println("  - Reported Playlist: " + report.getReportedPlaylist().getTitle());
            dtoBuilder.reportedPlaylist(new ReportDTO.ReportedPlaylistDTO(
                    report.getReportedPlaylist().getId(),
                    report.getReportedPlaylist().getTitle(),
                    report.getReportedPlaylist().getImageUrl(),
                    report.getReportedPlaylist().getOwner() != null ?
                            report.getReportedPlaylist().getOwner().getDisplayName() : "Unknown Creator"
            ));
        } else {
            System.out.println("  - Reported Playlist: NULL");
        }

        if (report.getReportedSongComment() != null) {
            System.out.println("  - Reported Song Comment: " + report.getReportedSongComment().getId());
            String authorName = (report.getReportedSongComment().getUser() != null) ?
                                 report.getReportedSongComment().getUser().getDisplayName() : "Unknown User";
            dtoBuilder.reportedSongComment(new ReportDTO.ReportedSongCommentDTO(
                    report.getReportedSongComment().getId(),
                    report.getReportedSongComment().getContent(),
                    authorName
            ));
        } else {
            System.out.println("  - Reported Song Comment: NULL");
        }

        if (report.getReportedPlaylistComment() != null) {
            System.out.println("  - Reported Playlist Comment: " + report.getReportedPlaylistComment().getId());
            String authorName = (report.getReportedPlaylistComment().getUser() != null) ?
                                 report.getReportedPlaylistComment().getUser().getDisplayName() : "Unknown User";
            dtoBuilder.reportedPlaylistComment(new ReportDTO.ReportedPlaylistCommentDTO(
                    report.getReportedPlaylistComment().getId(),
                    report.getReportedPlaylistComment().getContent(),
                    authorName
            ));
        } else {
            System.out.println("  - Reported Playlist Comment: NULL");
        }

        return dtoBuilder.build();
    }


    @Transactional(readOnly = true)
    public List<ReportDTO> getPendingReports() {
        return reportRepository.findAllByStatus(Report.ReportStatus.PENDING)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("Report not found with ID: " + reportId));
        report.setStatus(Report.ReportStatus.APPROVED);
        reportRepository.save(report);

        // TODO: Implement further actions based on report approval
        // e.g., send notification to reporter, take action on reported song/playlist
    }

    @Transactional
    public void rejectReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("Report not found with ID: " + reportId));
        report.setStatus(Report.ReportStatus.REJECTED);
        reportRepository.save(report);

        // TODO: Implement further actions based on report rejection
        // e.g., send notification to reporter
    }

    @Transactional
    public ReportDTO createReport(CreateReportDTO dto) {
        User reporter = getCurrentAuthenticatedUser();

        Report.ReportType reportType;
        Song reportedSong = null;
        Playlist reportedPlaylist = null;
        SongComment reportedSongComment = null;
        PlaylistComment reportedPlaylistComment = null;


        try {
            reportType = Report.ReportType.valueOf(dto.getEntityType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid entityType: " + dto.getEntityType() + ". Must be one of SONG, PLAYLIST, SONG_COMMENT, PLAYLIST_COMMENT.");
        }


        switch (reportType) {
            case SONG:
                reportedSong = songRepository.findById(dto.getEntityId())
                        .orElseThrow(() -> new NoSuchElementException("Song not found with ID: " + dto.getEntityId()));
                break;
            case PLAYLIST:
                reportedPlaylist = playlistRepository.findById(dto.getEntityId())
                        .orElseThrow(() -> new NoSuchElementException("Playlist not found with ID: " + dto.getEntityId()));
                break;
            case SONG_COMMENT:
                reportedSongComment = songCommentRepository.findById(dto.getEntityId())
                        .orElseThrow(() -> new NoSuchElementException("Song comment not found with ID: " + dto.getEntityId()));
                break;
            case PLAYLIST_COMMENT:
                reportedPlaylistComment = playlistCommentRepository.findById(dto.getEntityId())
                        .orElseThrow(() -> new NoSuchElementException("Playlist comment not found with ID: " + dto.getEntityId()));
                break;
        }

        Report newReport = Report.builder()
                .reason(dto.getReason())
                .reporter(reporter)
                .reportType(reportType)
                .reportedSong(reportedSong)
                .reportedPlaylist(reportedPlaylist)
                .reportedSongComment(reportedSongComment)
                .reportedPlaylistComment(reportedPlaylistComment)
                .status(Report.ReportStatus.PENDING)
                .build();

        newReport = reportRepository.save(newReport);
        return toDTO(newReport);
    }
}
