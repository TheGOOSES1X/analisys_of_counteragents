package Parser.utils;
import com.fasterxml.jackson.annotation.JsonProperty;
public class Okpd2Entry {
    private String id;
    private String name;
    private String title;

    // Геттеры и сеттеры
    @JsonProperty("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("title")
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}