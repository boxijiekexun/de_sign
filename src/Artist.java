import java.util.Comparator;

/**
 * Artist.java
 * 艺人实体类，实现了 Comparable 接口，并添加了资源路径字段。
 */
public class Artist implements Comparable<Artist> {
    private final String name;
    private final String genre; 
    private int popularity;
    
    // 【新增字段】用于存储本地资源路径
    private final String posterPath; 
    private final String audioPath;  

    // 【修改构造函数】接受五个参数
    public Artist(String name, String genre, int popularity, String posterPath, String audioPath) {
        this.name = name;
        this.genre = genre;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.audioPath = audioPath;
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public int getPopularity() {
        return popularity;
    }
    
    // 【新增 Getter】
    public String getPosterPath() {
        return posterPath;
    }
    
    // 【新增 Getter】
    public String getAudioPath() {
        return audioPath;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    /**
     * 实现 compareTo 方法，用于 PriorityQueue。
     */
    @Override
    public int compareTo(Artist other) {
        return other.popularity - this.popularity;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, 人气: %d)", name, genre, popularity);
    }

    /**
     * 用于创建 Max Heap 的 Comparator。
     */
    public static Comparator<Artist> popularityComparator() {
        return (a1, a2) -> a2.popularity - a1.popularity;
    }
}