package com.example.CMCmp3.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChartService {

    /**
     * Fake realtime data (có thể thay bằng query DB sau)
     */
    public Map<String, Object> getRealtime() {
        Map<String, Object> data = new HashMap<>();

        data.put("timeline", List.of("22:00","23:00","00:00","01:00","02:00","03:00","04:00","05:00"));

        data.put("vn",   List.of(38, 37, 35, 34, 33, 31, 30, 29));
        data.put("usuk", List.of(22, 22, 21, 21, 19, 18, 18, 17));
        data.put("kpop", List.of(18, 18, 17, 16, 15, 14, 14, 13));

        List<Map<String, Object>> top3 = new ArrayList<>();
        top3.add(Map.of("rank", 1, "percent", 38, "title", "Âm Thầm Bên Em", "artists", "Sơn Tùng", "cover", "/assets/SonTung.jpg"));
        top3.add(Map.of("rank", 2, "percent", 37, "title", "Khi Phải Quên Đi", "artists", "Phan Mạnh Quỳnh", "cover", "/assets/PhanManhQuynh.jpg"));
        top3.add(Map.of("rank", 3, "percent", 25, "title", "Anh Đã Không Biết Cách Yêu Em", "artists", "Quang Đăng Trần", "cover", "/assets/QuangDangTran.jpg"));

        data.put("top3", top3);

        return data;
    }

    public List<Map<String, String>> getWeeklyTiles() {
        return List.of(
                Map.of("code", "vn", "title", "BÀI HÁT VIỆT NAM", "cover", "/top100-vpop.png"),
                Map.of("code", "usuk", "title", "US-UK", "cover", "/top100.png"),
                Map.of("code", "kpop", "title", "K-POP", "cover", "/top100.png")
        );
    }
}
