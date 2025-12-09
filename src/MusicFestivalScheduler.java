import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import com.google.gson.Gson; // 确保已引入 Gson jar 包

/**
 * MusicFestivalScheduler.java
 * 主调度系统，封装所有数据结构和核心逻辑。
 * 修复版：集成了 WebSocket 可视化与核心业务逻辑。
 */
public class MusicFestivalScheduler {
    // 数据结构
    private final DoublyLinkedList timeline; // 时间轴
    private final PriorityQueue<Artist> hotArtists; // 热度榜 (Max Heap)
    private final Map<String, Performance> artistSchedule; // 快速查找 (Hash Map)
    private final PriorityQueue<FanReminder> reminders; // 粉丝提醒 (Min Heap)

    // 可视化组件
    private final SwarmWebSocketServer server;
    private final Gson gson;

    // --- 构造函数 ---

    // 构造函数
    public MusicFestivalScheduler(int port) {
        this.timeline = new DoublyLinkedList();
        this.hotArtists = new PriorityQueue<>(Artist.popularityComparator());
        this.artistSchedule = new HashMap<>();
        this.reminders = new PriorityQueue<>();

        this.gson = new Gson();
        
        // 【核心修改】这里传入 "this::generateCurrentStateJson"
        // 意思是：当服务器需要数据时，就来调用我的 generateCurrentStateJson 方法
        this.server = new SwarmWebSocketServer(port, this::generateCurrentStateJson);
        
        this.server.start();
        System.out.println("✅ Scheduler 就绪，WebSocket 服务器运行在端口: " + port);
    }

    // 兼容旧代码的无参构造函数 (默认 8080 端口)
    public MusicFestivalScheduler() {
        this(8080);
    }

    // --- 核心业务功能 ---

    /**
     * 1. 录入/导入艺人数据和演出场次
     */
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

    /**
     * 2. 快速查找艺人风格 (哈希表)
     */
    public String findArtistGenre(String artistName) {
        Performance p = artistSchedule.get(artistName);
        if (p != null) {
            return p.getArtist().getGenre();
        }
        return "未找到该艺人或未安排演出。";
    }

    /**
     * 3. 获取人气最高的 Top N 艺人热度榜 (优先队列)
     */
    public List<Artist> getHotArtistsRanking(int n) {
        // 创建副本以避免破坏原队列
        PriorityQueue<Artist> tempQueue = new PriorityQueue<>(hotArtists);
        // 使用 Java Stream 取前 N 个
        return tempQueue.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    // --- 粉丝提醒功能 ---

    /**
     * 4. 添加粉丝预约提醒
     */
    public void addFanReminder(String fanId, String artistName, long reminderTime) {
        Performance performance = artistSchedule.get(artistName);
        if (performance != null) {
            reminders.add(new FanReminder(fanId, performance, reminderTime));
            System.out.println("🔔 提醒已设置: 粉丝 " + fanId + " 预约了 " + artistName + " (提醒时间: " + reminderTime + "h)");
        } else {
            System.out.println("⚠️ 设置提醒失败: 未找到艺人 " + artistName);
        }
    }

    /**
     * 5. 处理实时提醒
     */
    public void processReminders(long currentTime) {
        System.out.println("\n--- ⏰ 实时提醒处理 (当前时间: " + currentTime + "h) ---");
        // 检查队首元素的提醒时间是否小于等于当前时间
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
     * 生成当前状态的 JSON 字符串
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
            
            // --- 【新增】代表作和海报信息注入 ---
            String artistName = p.getArtist().getName();
            String songTitle = "未知代表作";
            String imageKeyword = ""; // 用于 AI 搜索图片的关键词
            String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"; // 通用测试音频
            
            // 根据艺人分配代表作和图片关键词
            switch (artistName) {
                case "Beyond":
                    songTitle = "海阔天空";
                    imageKeyword = "Beyond Band Concert"; // 搜索 Beyond 乐队演唱会
                    break;
                case "周杰伦":
                    songTitle = "七里香";
                    imageKeyword = "Jay Chou Concert"; // 搜索 周杰伦演唱会
                    break;
                case "泰勒斯威夫特":
                    songTitle = "Love Story";
                    imageKeyword = "Taylor Swift Eras Tour"; // 搜索 泰勒斯威夫特
                    break;
                case "方大同":
                    songTitle = "爱爱爱";
                    imageKeyword = "Khalil Fong singing";
                    break;
                case "林俊杰":
                    songTitle = "江南";
                    imageKeyword = "JJ Lin Concert";
                    break;
                case "韩红":
                    songTitle = "天路";
                    imageKeyword = "Han Hong singer stage";
                    break;
                case "陶喆":
                    songTitle = "爱很简单";
                    imageKeyword = "David Tao concert";
                    break;
                default:
                    imageKeyword = p.getArtist().getGenre() + " Music Festival";
                    break;
            }
            
            data.put("songTitle", songTitle);
            data.put("audioUrl", audioUrl); // 使用通用测试音频
            
            // 自动搜索图片 (使用 Unsplash 随机图服务，加入关键词和哈希值保证变化)
           data.put("posterImage", "https://loremflickr.com/400" + imageKeyword.replace(" ", "-") + "&sig=" + artistName.hashCode());

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
            // 升序排列 (Min Heap)
            return Long.compare(this.reminderTime, other.reminderTime);
        }
    }
}
