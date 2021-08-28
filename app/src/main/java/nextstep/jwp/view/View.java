package nextstep.jwp.view;

public class View {

    private final String path;
    private final boolean empty;

    private View() {
        path = "";
        empty = true;
    }

    public View(String path) {
        this.path = path;
        this.empty = false;
    }

    public static View of(String path){
        return new View(path);
    }

    public static View empty() {
        return new View();
    }

    public String getPath() {
        return path;
    }

    public boolean isEmpty() {
        return empty;
    }
}
