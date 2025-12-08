import java.util.ArrayList;
import java.util.List;

/**
 * DoublyLinkedList.java
 * 自定义双向链表，用于管理演出时间轴。
 */
public class DoublyLinkedList {
    private Performance head;
    private Performance tail;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public Performance getHead() {
        return head;
    }

    // 检查冲突
    public boolean checkConflict(Performance newPerformance) {
        Performance current = head;
        while (current != null) {
            if (current != newPerformance && current.getTimeSlot().conflictsWith(newPerformance.getTimeSlot())) {
                // System.err.println("冲突检测: " + newPerformance + " 与 " + current + " 冲突!");
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // 插入演出
    public boolean insertPerformance(Performance newPerformance) {
        if (checkConflict(newPerformance)) {
            return false;
        }

        if (head == null) {
            head = newPerformance;
            tail = newPerformance;
            newPerformance.prev = null;
            newPerformance.next = null;
        } else {
            tail.next = newPerformance;
            newPerformance.prev = tail;
            newPerformance.next = null;
            tail = newPerformance;
        }
        return true;
    }

    // 移除演出
    public void removePerformance(Performance target) {
        if (target == null) return;

        if (target.prev != null) {
            target.prev.next = target.next;
        } else {
            head = target.next;
        }

        if (target.next != null) {
            target.next.prev = target.prev;
        } else {
            tail = target.prev;
        }
        
        target.prev = null;
        target.next = null;
    }

    // 调换位置 (简单实现：移除后重新插入)
    // 注意：实际项目中应交换节点指针，此处为演示简化处理
    public boolean swapPerformance(Performance p1, Performance p2) {
        if (p1 == p2) return true;

        System.out.println(">> 正在执行链表节点交换...");
        
        // 1. 移除两个节点
        removePerformance(p1);
        removePerformance(p2);

        // 2. 重新插入 (注意顺序，这里简单地按 p2 然后 p1 插入到尾部)
        // 在严谨的实现中，应该记录它们原先的插入位置索引
        insertPerformance(p2);
        insertPerformance(p1);
        
        return true;
    }

    // 获取完整列表
    public List<Performance> getTimeline() {
        List<Performance> list = new ArrayList<>();
        Performance current = head;
        while (current != null) {
            list.add(current);
            current = current.next;
        }
        return list;
    }
}
