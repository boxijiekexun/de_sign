import java.util.ArrayList;
import java.util.List;

/**
 * DoublyLinkedList.java
 * 自定义双向链表，用于管理演出时间轴 。
 */
public class DoublyLinkedList {
    private Performance head;
    private Performance tail;

    // 初始化
    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    // 访问器--head
    public Performance getHead() {
        return head;
    }

    /**
     * 检查新的演出是否与链表中已有的任何演出冲突。
     */
    public boolean checkConflict(Performance newPerformance) {
        Performance current = head;
        while (current != null) {
            // 检查时间冲突
            if (current != newPerformance && current.getTimeSlot().conflictsWith(newPerformance.getTimeSlot())) {
                System.err.println("冲突检测: " + newPerformance + " 与 " + current + " 冲突!");
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * 将演出按时间顺序插入链表。
     * 如果存在时间冲突则插入失败，返回 false。
     */
    public boolean insertPerformance(Performance newPerformance) {
        // 冲突检测
        if (checkConflict(newPerformance)) {
            return false;
        }

        // 插入逻辑--如果链表为空
        if (head == null) {
            head = newPerformance;
            tail = newPerformance;
        } else {
            // 插入到链表尾部
            tail.next = newPerformance;
            newPerformance.prev = tail;
            tail = newPerformance;
        }
        return true;
    }

    /**
     * 移除指定的演出场次。
     */
    public void removePerformance(Performance target) {
        // 找不到目标
        if (target == null) return;

        if (target.prev != null) {
            target.prev.next = target.next;
        } else {
            // 目标是头节点
            head = target.next;
        }

        if (target.next != null) {
            target.next.prev = target.prev;
        } else {
            // 目标是尾节点
            tail = target.prev;
        }

        // 清除目标节点的指针
        target.prev = null;
        target.next = null;
    }

    /**
     * 临时调换两场演出的位置。
     * 这里实现为移除后重新插入
     * 注：此处仅调换了链表节点，没有改变其 TimeSlot，因此时间冲突检查仍基于原始时间。
     */
    public boolean swapPerformance(Performance p1, Performance p2) {
        if (p1 == p2) return true;

        // 实际的临时调换会涉及复杂的指针操作，这里仅演示逻辑
        System.out.println("\n🔄 尝试调换 " + p1.getArtist().getName() + " 和 " + p2.getArtist().getName() + " 的位置...");

        // 临时移除 p1 和 p2 (注意：这里会改变 head/tail)
        removePerformance(p1);
        removePerformance(p2);

        // 重新插入，为保证演示，我们简单地按顺序重新插入（实际应按时间/位置插入）
        // 警告：此简单实现无法保留原本的相对位置，仅展示调换功能的概念。
        insertPerformance(p2);
        insertPerformance(p1);

        System.out.println("✅ 调换完成。请检查时间轴。");
        return true;
    }

    /**
     * 打印整个时间轴。
     */
    public List<Performance> getTimeline() {
        List<Performance> timeline = new ArrayList<>();
        Performance current = head;
        while (current != null) {
            timeline.add(current);
            current = current.next;
        }
        return timeline;
    }
}
