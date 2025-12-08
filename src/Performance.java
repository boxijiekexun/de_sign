/**
 * Performance.java
 * 演出场次类，作为双向链表的节点。
 * 演出类主要由艺人和时间段组成。
 * 通过管理这些节点，调度系统可以有效地安排和查询演出信息。
 */

public class Performance {
    private final Artist artist;
    private final TimeSlot timeSlot;

    // 双向链表指针
    public Performance prev;
    public Performance next;

    // 初始化
    public Performance(Artist artist, TimeSlot timeSlot) {
        this.artist = artist;
        this.timeSlot = timeSlot;
        this.prev = null;
        this.next = null;
    }

    // 访问器--artist
    public Artist getArtist() {
        return artist;
    }

    // 访问器--timeSlot
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    // 字符串表示---打印演出信息
    @Override
    public String toString() {
        return String.format("【%s】在 %s 演出", artist.getName(), timeSlot.toString());
    }
}
