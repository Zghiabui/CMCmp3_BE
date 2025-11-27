package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.ArtistDTO;
import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.repository.SongListenLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final SongService songService;
    private final SongListenLogRepository songListenLogRepository;

    // Cấu hình cho biểu đồ đường
    private static final int STEP_MINUTES = 120; // 2 giờ
    private static final int WINDOW_POINTS = 12; // 12 điểm trong 24 giờ
    private static final DateTimeFormatter HHmm = DateTimeFormatter.ofPattern("HH:mm");


    public Map<String, Object> getRealtime() {
        // === PHẦN 1: LẤY DỮ LIỆU CƠ BẢN VÀ CHUẨN BỊ ===

        // 1. Lấy danh sách 100 bài hát hàng đầu để xác định top 3 hiện tại
        List<SongDTO> top100Songs = songService.getTopSongs(100);
        List<SongDTO> currentTop3Songs = top100Songs.stream().limit(3).collect(Collectors.toList());

        // Căn chỉnh thời gian về mốc 2 giờ chẵn gần nhất (e.g., 15:30 -> 14:00)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime alignedNow = now.withMinute(0).withSecond(0).withNano(0);
        if (now.getHour() % 2 != 0) {
            alignedNow = alignedNow.minusHours(1);
        }

        // === PHẦN 2: TÍNH TOÁN DỮ LIỆU CHO BIỂU ĐỒ ĐƯỜNG (lineChartData) ===
        List<String> timeline = new ArrayList<>(WINDOW_POINTS);
        List<Map<String, Object>> lineChartData = new ArrayList<>(WINDOW_POINTS); // Cấu trúc mới

        for (int i = 0; i < WINDOW_POINTS; i++) {
            LocalDateTime endTime = alignedNow.minusMinutes((long) (WINDOW_POINTS - 1 - i) * STEP_MINUTES);
            LocalDateTime startTime = endTime.minusMinutes(STEP_MINUTES);

            timeline.add(endTime.format(HHmm));

            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("time", endTime.format(HHmm)); // Thêm mốc thời gian vào mỗi dataPoint

            // Lấy lượt nghe thô cho từng bài hát trong top 3 trong khoảng thời gian này
            Map<Long, Long> rawListensInInterval = new HashMap<>();
            long totalListensInInterval = 0L;
            for (SongDTO song : currentTop3Songs) {
                long listens = songListenLogRepository.countListensForSongBetween(song.getId(), startTime, endTime);
                rawListensInInterval.put(song.getId(), listens);
                totalListensInInterval += listens;
            }

            // Chuyển đổi sang phần trăm và thêm vào dataPoint
            for (SongDTO song : currentTop3Songs) {
                double percentage = 0.0;
                if (totalListensInInterval > 0) {
                    percentage = (double) rawListensInInterval.get(song.getId()) / totalListensInInterval * 100.0;
                }
                dataPoint.put("song_" + song.getId(), (double) Math.round(percentage)); // Làm tròn thành số nguyên
            }
            lineChartData.add(dataPoint);
        }


        // === PHẦN 3: TẠO METADATA CHO BIỂU ĐỒ ĐƯỜNG (lineChartMetadata) ===
        Map<String, Map<String, Object>> lineChartMetadata = new HashMap<>(); // Cấu trúc mới
        for (SongDTO song : currentTop3Songs) {
            Map<String, Object> songMeta = new HashMap<>();
            songMeta.put("id", song.getId());
            songMeta.put("title", song.getTitle());
            songMeta.put("artists", song.getArtists().stream().map(ArtistDTO::getName).collect(Collectors.joining(", ")));
            songMeta.put("cover", song.getImageUrl());
            lineChartMetadata.put("song_" + song.getId(), songMeta);
        }


        // === PHẦN 4: LẤY DỮ LIỆU BẢNG XẾP HẠNG CHUNG (tái sử dụng) ===

        List<Map<String, Object>> top3Formatted = new ArrayList<>();
        long totalTop3OverallListens = currentTop3Songs.stream()
                                                    .mapToLong(SongDTO::getListenCount)
                                                    .sum();

        for (int i = 0; i < currentTop3Songs.size(); i++) {
            SongDTO song = currentTop3Songs.get(i);
            Map<String, Object> row = new HashMap<>();
            row.put("rank", i + 1);
            row.put("id", song.getId());
            row.put("title", song.getTitle());
            String artists = song.getArtists().stream().map(ArtistDTO::getName).collect(Collectors.joining(", "));
            row.put("artists", artists);
            row.put("cover", song.getImageUrl());
            row.put("listenCount", song.getListenCount());
            double percentage = 0.0;
            if (totalTop3OverallListens > 0) {
                percentage = (double) song.getListenCount() / totalTop3OverallListens * 100.0;
            }
            row.put("percent", (double) Math.round(percentage)); // Làm tròn thành số nguyên
            top3Formatted.add(row);
        }

        List<Map<String, Object>> chartItems = new ArrayList<>();
        for (int i = 0; i < top100Songs.size(); i++) {
            SongDTO song = top100Songs.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i + 1);
            item.put("id", song.getId());
            item.put("title", song.getTitle());
            String artists = song.getArtists().stream().map(ArtistDTO::getName).collect(Collectors.joining(", "));
            item.put("artists", artists);
            item.put("cover", song.getImageUrl());
            item.put("duration", song.getDuration());
            item.put("listenCount", song.getListenCount());
            chartItems.add(item);
        }

        // === PHẦN 5: KẾT HỢP VÀ TRẢ VỀ ===

        return Map.of(
                "timeline", timeline,
                "lineChartData", lineChartData,
                "lineChartMetadata", lineChartMetadata,
                "top3", top3Formatted,
                "items", chartItems,
                "lastUpdated", System.currentTimeMillis()
        );
    }

    public List<Map<String, String>> getWeeklyTiles() {
        return List.of(
                Map.of("code","vn","title","BÀI HÁT VIỆT NAM","cover","/top100-vpop.png"),
                Map.of("code","usuk","title","US-UK","cover","/top100.png"),
                Map.of("code","kpop","title","K-POP","cover","/top100.png")
        );
    }
}
