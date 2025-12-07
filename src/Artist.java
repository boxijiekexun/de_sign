import java.util.Comparator;

/**
 * Artist.java
 * 艺人实体类，实现了 Comparable 接口，以便在优先队列中按人气排序。
 */
public class Artist implements Comparable<Artist> {
    private final String name;
    private final String genre; // 摇滚/流行/电子 [cite: 104]
    private int popularity;     // 人气值 (越高越优先) [cite: 103]

    public Artist(String name, String genre, int popularity) {
        this.name = name;
        this.genre = genre;
        this.popularity = popularity;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    /**
     * 实现 compareTo 方法，用于 PriorityQueue。
     * 采用 Max Heap (最大堆)，即人气值越大的，优先级越高。
     */
    @Override
    public int compareTo(Artist other) {
        // 降序排列 (Max Heap): this.popularity - other.popularity > 0 时，this 优先
        return other.popularity - this.popularity;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, 人气: %d)", name, genre, popularity);
    }

    /**
     * 用于创建 Max Heap 的 Comparator (与 compareTo 效果一致，但更灵活)。
     */
    public static Comparator<Artist> popularityComparator() {
        return (a1, a2) -> a2.popularity - a1.popularity;
    }
}