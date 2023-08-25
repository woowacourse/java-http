package cache.com.example;

public class Greeting {

    private final long id;
    private final String content;

    public Greeting(String content) {
        this.id = 1;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
