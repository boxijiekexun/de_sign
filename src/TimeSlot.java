/**
 * TimeSlot.java
 * 描述一场演出的开始和结束时间。
 */
public class TimeSlot {
    private final int startTime; // 小时数，例如：14 (2 PM)
    private final int endTime; // 小时数，例如：16 (4 PM)

    // 初始化--确保开始时间和结束时间
    public TimeSlot(int startTime, int endTime) {
        if (startTime >= endTime) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间。");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 访问器--开始时间
    public int getStartTime() {
        return startTime;
    }

    // 访问器--结束时间
    public int getEndTime() {
        return endTime;
    }

    /**
     * 检查当前时间段是否与另一个时间段冲突。
     * 冲突条件：两个时间段有重叠。
     */
    public boolean conflictsWith(TimeSlot other) {
        // A开始在B结束前 并且 A结束在B开始后
        return this.startTime < other.endTime && this.endTime > other.startTime;
    }

    // 字符串表示---打印时间段信息
    @Override
    public String toString() {
        return String.format("[%02d:00 - %02d:00]", startTime, endTime);
    }
}
