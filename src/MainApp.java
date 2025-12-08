import java.util.List;

/**
 * MainApp.java
 * 主应用类，用于演示 MusicFestivalScheduler 的功能。
 */
public class MainApp {

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
        MusicFestivalScheduler scheduler = new MusicFestivalScheduler();

        // --- 1. 数据录入/导入 (哈希表 + 双向链表 + 优先队列更新) ---

        // 艺人数据 (Name, Genre, Popularity)
        Artist artistA = new Artist("RockStar", "摇滚", 95);
        Artist artistB = new Artist("PopQueen", "流行", 100); // 顶流
        Artist artistC = new Artist("DJ_Elec", "电子", 80);
        Artist artistD = new Artist("IndieBoy", "流行", 70);

        // 演出时间 (StartHour, EndHour)
        TimeSlot slot1 = new TimeSlot(14, 16); // 2PM-4PM
        TimeSlot slot2 = new TimeSlot(16, 18); // 4PM-6PM
        TimeSlot slot3 = new TimeSlot(18, 20); // 6PM-8PM
        TimeSlot slot4 = new TimeSlot(20, 22); // 8PM-10PM

        // 正常排期
        scheduler.addPerformance(artistA, slot1); // 摇滚
        scheduler.addPerformance(artistC, slot2); // 电子
        scheduler.addPerformance(artistB, slot4); // 顶流 流行
        scheduler.addPerformance(artistD, slot3); // 流行

        // --- 2. 冲突检测演示 (双向链表 `checkConflict` 逻辑) ---

        System.out.println("\n---  冲突检测演示 ---");
        // 尝试插入与 A 冲突的演出 (15:00-17:00)
        scheduler.addPerformance(new Artist("NewBand", "摇滚", 60), new TimeSlot(15, 17));

        printTimeline(scheduler.getTimeline());

        // --- 3. 艺人风格快速匹配 (哈希表) ---

        System.out.println("\n--- 🔍 艺人特长风格快速匹配 (哈希表) ---");
        String genre = scheduler.findArtistGenre("DJ_Elec");// [cite: 104]
        System.out.println("DJ_Elec 的风格是: " + genre);
        genre = scheduler.findArtistGenre("NonExist");
        System.out.println("NonExist 的风格是: " + genre);

        // --- 4. 人气热度榜 (优先队列) ---

        System.out.println("\n---  人气艺人热度榜 (优先队列) ---");
        List<Artist> top2 = scheduler.getHotArtistsRanking(2); // [cite: 103]
        System.out.println("今日 Top 2 艺人：");
        for (int i = 0; i < top2.size(); i++) {
            System.out.printf("  %d. %s\n", i + 1, top2.get(i).toString());
        }

        // --- 5. 临时调换 (双向链表操作) ---

        // 模拟临时调换 RockStar (14-16) 和 IndieBoy (18-20) 的位置 [cite: 105]
        Performance p1 = scheduler.getPerformanceByArtistName("RockStar");
        Performance p2 = scheduler.getPerformanceByArtistName("IndieBoy");
        scheduler.getTimeline().swapPerformance(p1, p2);

        printTimeline(scheduler.getTimeline());


        // --- 6. 粉丝预约提醒和处理 (优先队列) ---

        // 假设当前时间是 13:00 (13h)
        long currentTime = 13;

        // 预约提醒设置 (ReminderTime - 小时数)
        // 假设 RockStar (14h开始), DJ_Elec (16h开始)
        scheduler.addFanReminder("Fan_001", "RockStar", 13); // 即将开始，需立即提醒
        scheduler.addFanReminder("Fan_002", "DJ_Elec", 15); // 15h时提醒
        scheduler.addFanReminder("Fan_003", "PopQueen", 21); // 21h时提醒

        // 第一次处理：当前时间 13h，触发 Fan_001 的提醒
        scheduler.processReminders(currentTime); //

        // 时间流逝到 16h
        currentTime = 16;

        // 第二次处理：当前时间 16h，触发 Fan_002 的提醒
        scheduler.processReminders(currentTime); //
    }
}
