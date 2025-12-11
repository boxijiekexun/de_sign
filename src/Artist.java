import java.util.Comparator;

/**
 * Artist.java
 * 艺人实体类，增加了代表作和图片 URL 字段。
 */
public class Artist implements Comparable<Artist> {
    private final String name;
    private final String genre; // 摇滚/流行/电子 
    private int popularity;     // 人气值 (越高越优先) 
    private final String masterpieceUrl; // 代表作音乐链接
    private final String imageUrl; // 海报图片链接

    // 【修改】构造函数，增加新的字段
    public Artist(String name, String genre, int popularity, String masterpieceUrl, String imageUrl) {
        this.name = name;
        this.genre = genre;
        this.popularity = popularity;
        this.masterpieceUrl = masterpieceUrl; 
        this.imageUrl = imageUrl;             
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

    // 设置人气值
    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }
    
    // 访问器--代表作 URL
    public String getMasterpieceUrl() {
        return masterpieceUrl;
    }

    // 访问器--图片 URL
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 实现 compareTo 方法，用于 PriorityQueue (Max Heap)。
     */
    @Override
    public int compareTo(Artist other) {
        return other.popularity - this.popularity;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, 人气: %d)", name, genre, popularity);
    }

    public static Comparator<Artist> popularityComparator() {
        return (a1, a2) -> a2.popularity - a1.popularity;
    }
}