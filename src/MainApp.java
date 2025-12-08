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

        // --- 2. 数据录入 ---

        // 定义艺人
        Artist artistA = new Artist("RockStar", "摇滚", 95);
        Artist artistB = new Artist("PopQueen", "流行", 100); 
        Artist artistC = new Artist("DJ_Elec", "电子", 80);
        Artist artistD = new Artist("IndieBoy", "流行", 70);

        // 定义时间段
        TimeSlot slot1 = new TimeSlot(14, 16); 
        TimeSlot slot2 = new TimeSlot(16, 18); 
        TimeSlot slot3 = new TimeSlot(18, 20); 
        TimeSlot slot4 = new TimeSlot(20, 22); 

        // 录入演出
        scheduler.addPerformance(artistA, slot1); 
        scheduler.addPerformance(artistC, slot2); 
        scheduler.addPerformance(artistB, slot4); 
        scheduler.addPerformance(artistD, slot3); 

        // --- 3. 冲突检测演示 ---
        System.out.println("\n--- ⚠️ 冲突检测演示 ---");
        scheduler.addPerformance(new Artist("NewBand", "摇滚", 60), new TimeSlot(15, 17));

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
