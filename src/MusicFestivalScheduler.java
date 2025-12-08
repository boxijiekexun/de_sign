import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * MusicFestivalScheduler.java
 * 主调度系统，封装所有数据结构和核心逻辑。
 */
public class MusicFestivalScheduler {
    // 数据结构：
    private final DoublyLinkedList timeline; // 双向链表：时间轴上的演出顺序 
    private final PriorityQueue<Artist> hotArtists; // 优先队列：按人气排序的艺人热度榜 
    private final Map<String, Performance> artistSchedule; // 哈希表：艺人档期快速匹配 (Key: 艺人名, Value: 演出) 
    private final PriorityQueue<FanReminder> reminders; // 优先队列：处理粉丝预约提醒 

    // 初始化--默认构造函数
    public MusicFestivalScheduler() {
        this.timeline = new DoublyLinkedList();
        // 初始化 PriorityQueue 使用自定义 Comparator 实现 Max Heap 
        this.hotArtists = new PriorityQueue<>(Artist.popularityComparator());
        this.artistSchedule = new HashMap<>(); // 哈希表初始化
        // FanReminder 队列使用默认 Min Heap (按提醒时间升序，假设 FanReminder 实现 Comparable)
        this.reminders = new PriorityQueue<>();
    }

    // 初始化--带参数构造函数
    public MusicFestivalScheduler(DoublyLinkedList timeline, PriorityQueue<Artist> hotArtists, Map<String, Performance> artistSchedule, PriorityQueue<FanReminder> reminders) {
        this.timeline = timeline;
        this.hotArtists = hotArtists;
        this.artistSchedule = artistSchedule;
        this.reminders = reminders;
    }

    // --- 核心调度功能 ---

    /**
     * 录入/导入艺人数据和演出场次。
     */
    public boolean addPerformance(Artist artist, TimeSlot timeSlot) {
        Performance newPerformance = new Performance(artist, timeSlot);

        // 1. 冲突检测和插入时间轴 (双向链表)
        if (!timeline.insertPerformance(newPerformance)) {
            System.err.println("录入失败：" + newPerformance.getArtist().getName() + " 存在时间冲突。");
            return false;
        }

        // 2. 更新热度榜 (优先队列)
        hotArtists.add(artist);

        // 3. 更新档期哈希表 (哈希表)
        artistSchedule.put(artist.getName(), newPerformance);

        System.out.println("成功录入演出: " + newPerformance.toString());
        return true;
    }

    /**
     * 快速查找艺人风格。
     */
    public String findArtistGenre(String artistName) {
        Performance p = artistSchedule.get(artistName);
        if (p != null) {
            return p.getArtist().getGenre();
        }
        return "未找到该艺人或未安排演出。";
    }

    /**
     * 获取人气最高的 Top N 艺人热度榜 。
     * 注意：这里会清空/改变原队列，实际应用中应使用迭代器或克隆。
     */
    public List<Artist> getHotArtistsRanking(int n) {
        // 为了不破坏原队列，我们克隆一个队列进行操作
        PriorityQueue<Artist> tempQueue = new PriorityQueue<>(hotArtists);

        List<Artist> ranking = tempQueue.stream()
                .limit(n)
                .collect(Collectors.toList());
        return ranking;
    }

    // --- 粉丝提醒功能 ---

    /**
     * 粉丝预约提醒 。
     * FanReminder 假设实现 Comparable，并按其提醒时间进行排序 (Min Heap)。
     */
    public void addFanReminder(String fanId, String artistName, long reminderTime) {
        Performance performance = artistSchedule.get(artistName);
        if (performance != null) {
            reminders.add(new FanReminder(fanId, performance, reminderTime));
            System.out.println("提醒已设置: 粉丝 " + fanId + " 预约了 " + artistName);
        } else {
            System.out.println("设置提醒失败: 未找到艺人 " + artistName + " 的演出。");
        }
    }

    /**
     * 模拟处理实时提醒 (在当前时间点) 。
     */
    public void processReminders(long currentTime) {
        System.out.println("\n--- 实时提醒处理 (当前时间: " + currentTime + "h) ---");
        while (!reminders.isEmpty() && reminders.peek().getReminderTime() <= currentTime) {
            FanReminder reminder = reminders.poll();
            System.out.println("通知粉丝 " + reminder.getFanId() + ": " + reminder.getPerformance().getArtist().getName()
                    + " 即将开始！");
        }
        System.out.println("------------------------------------------");
    }

    // --- 辅助功能 ---
    // 访问器方法
    public DoublyLinkedList getTimeline() {
        return timeline;
    }

    // 访问器方法
    public Performance getPerformanceByArtistName(String artistName) {
        // artistSchedule 的 Key 是艺人名称，Value 是 Performance 对象
        return artistSchedule.get(artistName);
    }

    /**
     * 辅助类：粉丝提醒，按时间排序
     */
    private static class FanReminder implements Comparable<FanReminder> {
        private final String fanId;
        private final Performance performance;
        private final long reminderTime; // 提醒时间 (小时数)

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
            // Min Heap: 时间早的优先
            return Long.compare(this.reminderTime, other.reminderTime);
        }
    }
}
