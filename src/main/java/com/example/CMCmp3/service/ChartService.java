package com.example.CMCmp3.service;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ChartService {

    // ====== Cấu hình "mỗi 2 giờ" ======
    private static final int STEP_MINUTES  = 120;   // bước thời gian: 2 giờ
    private static final int WINDOW_POINTS = 12;    // 24h / 2h = 12 điểm
    // ===================================

    private final Deque<Point> buffer = new ArrayDeque<>();
    private static final DateTimeFormatter HHmm = DateTimeFormatter.ofPattern("HH:mm");
    private final Random rnd = new Random();

    record Point(LocalDateTime ts, int vn, int usuk, int kpop) {}

    @PostConstruct
    void init() {
        // Căn mốc 2 giờ chẵn gần nhất (…, 00:00, 02:00, 04:00, …)
        LocalDateTime now = LocalDateTime.now();
        int alignHour = (now.getHour() / 2) * 2; // 0,2,4,...
        LocalDateTime aligned = now.withHour(alignHour).withMinute(0).withSecond(0).withNano(0);

        // Khởi tạo 24h gần nhất (12 điểm cách nhau 2h)
        LocalDateTime t = aligned.minusMinutes((long) STEP_MINUTES * (WINDOW_POINTS - 1));
        int vn = 35, usuk = 21, kpop = 17;
        for (int i = 0; i < WINDOW_POINTS; i++) {
            int[] p = next(vn, usuk, kpop);
            buffer.addLast(new Point(t, p[0], p[1], p[2]));
            t = t.plusMinutes(STEP_MINUTES);
            vn = p[0]; usuk = p[1]; kpop = p[2];
        }
    }

    // Chạy đúng mỗi 2 giờ (phút 00, giây 00): 00:00, 02:00, 04:00, ...
    @Scheduled(cron = "0 0 */2 * * *")
    void tick() {
        Point last = buffer.peekLast();
        if (last == null) return;

        // Căn thời điểm hiện tại về mốc 2h chẵn
        LocalDateTime now = LocalDateTime.now();
        int alignHour = (now.getHour() / 2) * 2;
        LocalDateTime alignedNow = now.withHour(alignHour).withMinute(0).withSecond(0).withNano(0);

        // Nếu buffer đã có điểm cho mốc này thì bỏ qua
        if (last.ts.equals(alignedNow)) return;

        int[] p = next(last.vn, last.usuk, last.kpop);
        buffer.addLast(new Point(alignedNow, p[0], p[1], p[2]));
        while (buffer.size() > WINDOW_POINTS) buffer.removeFirst();
    }

    private int[] next(int vn, int usuk, int kpop) {
        // Random-walk nhẹ cho demo; sau này thay bằng dữ liệu thật
        vn   = clamp(vn   + rnd.nextInt(5) - 2, 10, 60);
        usuk = clamp(usuk + rnd.nextInt(5) - 2, 10, 60);
        kpop = clamp(kpop + rnd.nextInt(5) - 2, 10, 60);

        double sum = vn + usuk + kpop;
        if (sum <= 0) sum = 1; // tránh chia 0

        // Chuẩn hoá về % và đảm bảo tổng 100
        int vnPct   = (int) Math.round(vn   / sum * 100.0);
        int usukPct = (int) Math.round(usuk / sum * 100.0);
        int kpopPct = Math.max(0, 100 - vnPct - usukPct);

        return new int[]{vnPct, usukPct, kpopPct};
    }
    private int clamp(int v, int lo, int hi){ return Math.max(lo, Math.min(hi, v)); }

    public Map<String, Object> getRealtime() {
        List<String> timeline = new ArrayList<>(WINDOW_POINTS);
        List<Integer> vnVals   = new ArrayList<>(WINDOW_POINTS);
        List<Integer> usukVals = new ArrayList<>(WINDOW_POINTS);
        List<Integer> kpopVals = new ArrayList<>(WINDOW_POINTS);

        for (Point p : buffer) {
            timeline.add(p.ts.format(HHmm)); // 00:00, 02:00, 04:00, ...
            vnVals.add(p.vn);
            usukVals.add(p.usuk);
            kpopVals.add(p.kpop);
        }

        // Giá trị mới nhất
        int lastVN = vnVals.get(vnVals.size()-1);
        int lastUS = usukVals.get(usukVals.size()-1);
        int lastKP = kpopVals.get(kpopVals.size()-1);

        // Metadata cho từng series (để FE map đúng đường line)
        Map<String, Object> seriesMeta = Map.of(
                "vn",   Map.of("title", "Âm Thầm Bên Em",                 "artists", "Sơn Tùng",          "cover", "/assets/SonTung.jpg"),
                "usuk", Map.of("title", "Khi Phải Quên Đi",               "artists", "Phan Mạnh Quỳnh",   "cover", "/assets/PhanManhQuynh.jpg"),
                "kpop", Map.of("title", "Anh Đã Không Biết Cách Yêu Em",  "artists", "Quang Đăng Trần",   "cover", "/assets/QuangDangTran.jpg")
        );

        // Top 3: sort theo phần trăm ↓ rồi gán rank, giữ lại code để FE biết thuộc series nào
        List<Map<String, Object>> items = new ArrayList<>(List.of(
                new HashMap<>(Map.of("code","vn",   "percent", lastVN)),
                new HashMap<>(Map.of("code","usuk", "percent", lastUS)),
                new HashMap<>(Map.of("code","kpop", "percent", lastKP))
        ));
        items.sort((a, b) -> Integer.compare((Integer) b.get("percent"), (Integer) a.get("percent")));

        List<Map<String, Object>> top3 = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String code = (String) items.get(i).get("code");
            int percent = (Integer) items.get(i).get("percent");
            Map<String, Object> meta = (Map<String, Object>) seriesMeta.get(code);

            Map<String, Object> row = new HashMap<>();
            row.put("rank", i + 1);
            row.put("code", code);            // vn/usuk/kpop
            row.put("percent", percent);
            row.put("title",  meta.get("title"));
            row.put("artists",meta.get("artists"));
            row.put("cover",  meta.get("cover")); // tuỳ bạn có file hay không
            top3.add(row);
        }

        return Map.of(
                "timeline", timeline,
                "vn", vnVals,
                "usuk", usukVals,
                "kpop", kpopVals,
                "seriesMeta", seriesMeta,     // FE có thể dùng để đặt nhãn/tooltip theo tên bài
                "top3", top3,                 // đã sort + có code để map đúng line
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
