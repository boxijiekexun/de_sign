import java.util.List;

/**
 * MainApp.java
 * 主应用类，用于演示 MusicFestivalScheduler 的功能。
 */
public class MainApp {

    // 打印时间轴的辅助方法
    private static void printTimeline(DoublyLinkedList timeline) {
        System.out.println("\n--- 🎼 演出时间轴 (双向链表) ---");
        List<Performance> performances = timeline.getTimeline();
        if (performances.isEmpty()) {
            System.out.println("时间轴为空。");
            return;
        }
        for (int i = 0; i < performances.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, performances.get(i).toString());
        }
        System.out.println("-------------------------------------");
    }

    public static void main(String[] args) {
        // 1. 初始化调度器 (启动 WebSocket 端口 8080)
    System.out.println(">>> 系统启动中...");
    MusicFestivalScheduler scheduler = new MusicFestivalScheduler(8080);
    
    System.out.println("\n🌐 提示: 请在浏览器打开 index.html 查看 3D 可视化效果。\n");

    // --- 2. 数据录入：使用新艺人列表 ---

    // 定义艺人 (Name, Genre, Popularity)
    // 风格关键词将用于前端图片搜索
    Artist artistA = new Artist("Beyond", "摇滚", 98, null, null);      // 摇滚 (Rock)
    Artist artistB = new Artist("周杰伦", "流行/R&B", 100, null, null);  // 流行/R&B (Pop/R&B)
    Artist artistC = new Artist("泰勒斯威夫特", "流行", 95, null, null);  // 流行 (Pop)
    Artist artistD = new Artist("方大同", "R&B/灵魂乐", 85, null, null); // R&B/灵魂乐 (Soul/R&B)
    Artist artistE = new Artist("林俊杰", "流行/抒情", 93, null, null);   // 流行/抒情 (Pop Ballad)
    Artist artistF = new Artist("韩红", "民歌/流行", 90, null, null);      // 民族/流行 (Folk Pop)
    Artist artistG = new Artist("陶喆", "R&B/摇滚", 88, null, null);      // R&B/摇滚 (R&B/Rock)
    
    // 演出时间 (StartHour, EndHour)
    // 我们将这些演出错开，避免冲突
    TimeSlot slot1 = new TimeSlot(14, 16); // 14:00-16:00
    TimeSlot slot2 = new TimeSlot(16, 17); // 16:00-17:00
    TimeSlot slot3 = new TimeSlot(17, 18); // 17:00-18:00
    TimeSlot slot4 = new TimeSlot(18, 20); // 18:00-20:00 (较长时段)
    TimeSlot slot5 = new TimeSlot(20, 21); // 20:00-21:00
    TimeSlot slot6 = new TimeSlot(21, 22); // 21:00-22:00
    TimeSlot slot7 = new TimeSlot(22, 23); // 22:00-23:00

    // 录入演出，注意排期
    scheduler.addPerformance(artistA, slot1); // Beyond (摇滚)
    scheduler.addPerformance(artistG, slot2); // 陶喆 (R&B/摇滚)
    scheduler.addPerformance(artistF, slot3); // 韩红 (民歌/流行)
    scheduler.addPerformance(artistB, slot4); // 周杰伦 (顶流，长时段)
    scheduler.addPerformance(artistC, slot5); // 泰勒斯威夫特 (流行)
    scheduler.addPerformance(artistE, slot6); // 林俊杰 (流行/抒情)
    scheduler.addPerformance(artistD, slot7); // 方大同 (R&B/灵魂乐)


        // --- 3. 冲突检测演示 ---
        System.out.println("\n--- ⚠️ 冲突检测演示 ---");
        scheduler.addPerformance(new Artist("NewBand", "摇滚", 60, null, null), new TimeSlot(15, 17));

        printTimeline(scheduler.getTimeline());

        // --- 4. 艺人风格快速匹配 (哈希表) ---
        System.out.println("\n--- 🔍 艺人特长风格快速匹配 ---");
        System.out.println("DJ_Elec 的风格: " + scheduler.findArtistGenre("DJ_Elec"));
        System.out.println("NonExist 的风格: " + scheduler.findArtistGenre("NonExist"));

        // --- 5. 人气热度榜 (优先队列) ---
        System.out.println("\n--- 🏆 人气艺人热度榜 (Top 2) ---");
        List<Artist> top2 = scheduler.getHotArtistsRanking(2);
        for (int i = 0; i < top2.size(); i++) {
            System.out.printf("  Top %d: %s\n", i + 1, top2.get(i).toString());
        }

        // --- 6. 临时调换 (双向链表) ---
        System.out.println("\n--- 🔄 临时调换演示 (RockStar <-> IndieBoy) ---");
        Performance p1 = scheduler.getPerformanceByArtistName("RockStar");
        Performance p2 = scheduler.getPerformanceByArtistName("IndieBoy");
        
        if (p1 != null && p2 != null) {
            scheduler.getTimeline().swapPerformance(p1, p2);
            // 手动触发广播，更新前端
            scheduler.broadcastCurrentState();
        } else {
            System.out.println("找不到指定的演出，无法调换。");
        }

        printTimeline(scheduler.getTimeline());

        // --- 7. 粉丝预约提醒 (优先队列) ---
        System.out.println("\n--- 🔔 粉丝预约提醒演示 ---");
        
        // 设置提醒
        scheduler.addFanReminder("Fan_001", "RockStar", 13); // 13点提醒
        scheduler.addFanReminder("Fan_002", "DJ_Elec", 15);  // 15点提醒
        scheduler.addFanReminder("Fan_003", "PopQueen", 21); // 21点提醒

        // 模拟时间推移
        scheduler.processReminders(13); // 当前时间 13:00
        scheduler.processReminders(16); // 当前时间 16:00
    }
}
