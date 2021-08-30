package nextstep.jwp.httpserver.domain;

public class View {
    private String viewName;
    private String content;

    public View(String viewName) {
        this(viewName, null);
    }

    public View(String viewName, String content) {
        this.viewName = viewName;
        this.content = content;
    }

    public String getViewName() {
        return viewName;
    }

    public String getContent() {
        return content;
    }
}
