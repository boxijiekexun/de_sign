import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import com.google.gson.Gson; 

/**
 * MusicFestivalScheduler.java
 * 主调度系统，封装所有数据结构和核心逻辑。
 */
public class MusicFestivalScheduler {
    // 数据结构
    private final DoublyLinkedList timeline; 
    private final PriorityQueue<Artist> hotArtists; 
    private final Map<String, Performance> artistSchedule; 
    private final PriorityQueue<FanReminder> reminders; 

    // 可视化组件
    private final SwarmWebSocketServer server;
    private final Gson gson;

    // --- 构造函数 ---

    public MusicFestivalScheduler(int port) {
        this.timeline = new DoublyLinkedList();
        this.hotArtists = new PriorityQueue<>(Artist.popularityComparator());
        this.artistSchedule = new HashMap<>();
        this.reminders = new PriorityQueue<>();

        this.gson = new Gson();
        
        this.server = new SwarmWebSocketServer(port, this::generateCurrentStateJson);
        
        this.server.start();
        System.out.println("✅ Scheduler 就绪，WebSocket 服务器运行在端口: " + port);
    }

    public MusicFestivalScheduler() {
        this(8080);
    }

    // --- 核心业务功能 ---

    public boolean addPerformance(Artist artist, TimeSlot timeSlot) {
        Performance newPerformance = new Performance(artist, timeSlot);

        // A. 冲突检测和插入时间轴 (双向链表)
        if (!timeline.insertPerformance(newPerformance)) {
            System.err.println("❌ 录入失败：" + newPerformance.getArtist().getName() + " 存在时间冲突。");
            return false;
        }

        // B. 更新热度榜 (优先队列)
        hotArtists.add(artist);

        // C. 更新档期哈希表 (哈希表)
        artistSchedule.put(artist.getName(), newPerformance);

        System.out.println("✅ 成功录入演出: " + newPerformance.toString());

        // D. 广播更新给前端
        broadcastCurrentState();
        return true;
    }

    public String findArtistGenre(String artistName) {
        Performance p = artistSchedule.get(artistName);
        if (p != null) {
            return p.getArtist().getGenre();
        }
        return "未找到该艺人或未安排演出。";
    }

    public List<Artist> getHotArtistsRanking(int n) {
        // 创建副本以避免破坏原队列
        PriorityQueue<Artist> tempQueue = new PriorityQueue<>(hotArtists);
        // 使用 Java Stream 取前 N 个
        return tempQueue.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    // --- 粉丝提醒功能 ---

    public void addFanReminder(String fanId, String artistName, long reminderTime) {
        Performance performance = artistSchedule.get(artistName);
        if (performance != null) {
            reminders.add(new FanReminder(fanId, performance, reminderTime));
            System.out.println("🔔 提醒已设置: 粉丝 " + fanId + " 预约了 " + artistName + " (提醒时间: " + reminderTime + "h)");
        } else {
            System.out.println("⚠️ 设置提醒失败: 未找到艺人 " + artistName);
        }
    }

    public void processReminders(long currentTime) {
        System.out.println("\n--- ⏰ 实时提醒处理 (当前时间: " + currentTime + "h) ---");
        while (!reminders.isEmpty() && reminders.peek().getReminderTime() <= currentTime) {
            FanReminder reminder = reminders.poll();
            System.out.println("📩 [发送通知] 粉丝 " + reminder.getFanId() + ": 您的艺人 " 
                    + reminder.getPerformance().getArtist().getName() + " 即将登台！");
        }
    }

    // --- 辅助 Getter (供 MainApp 使用) ---

    public DoublyLinkedList getTimeline() {
        return timeline;
    }

    public Performance getPerformanceByArtistName(String artistName) {
        return artistSchedule.get(artistName);
    }

    // --- 可视化与广播功能 ---

    /**
     * 生成当前状态的 JSON 字符串 (包含图片和音乐 URL)
     */
    public String generateCurrentStateJson() {
        List<Performance> currentPerformances = timeline.getTimeline();
        List<Map<String, Object>> visualData = new ArrayList<>();
        int index = 0;

        for (Performance p : currentPerformances) {
            Map<String, Object> data = new HashMap<>();
            data.put("index", index++);
            data.put("artist", p.getArtist().getName());
            data.put("genre", p.getArtist().getGenre());
            data.put("startTime", p.getTimeSlot().getStartTime());
            data.put("endTime", p.getTimeSlot().getEndTime());
            data.put("popularity", p.getArtist().getPopularity());
            // 【新增】海报图片 URL
            data.put("imageUrl", p.getArtist().getImageUrl()); 
            // 【新增】代表作音乐 URL
            data.put("masterpieceUrl", p.getArtist().getMasterpieceUrl()); 
            visualData.add(data);
        }
        return gson.toJson(visualData);
    }

    /**
     * 广播状态
     */
    public void broadcastCurrentState() {
        if (server != null) {
            server.broadcast(generateCurrentStateJson());
        }
    }

    // --- 内部类: FanReminder ---
    private static class FanReminder implements Comparable<FanReminder> {
        private final String fanId;
        private final Performance performance;
        private final long reminderTime;

        public FanReminder(String fanId, Performance performance, long reminderTime) {
            this.fanId = fanId;
            this.performance = performance;
            this.reminderTime = reminderTime;
        }

        public String getFanId() { return fanId; }
        public Performance getPerformance() { return performance; }
        public long getReminderTime() { return reminderTime; }

        @Override
        public int compareTo(FanReminder other) {
            return Long.compare(this.reminderTime, other.reminderTime);
        }
    }
}
