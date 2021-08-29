package nextstep.jwp.view;

public class View {

    private static final String DEFAULT_DIRECTORY = "static";
    private static final String DEFAULT_PATH = "/index";
    private static final String HTML_EXTENSION = ".html";

    private final String name;

    public View(String name) {
        this.name = name;
    }

    public String getFilePath() {
        if (name.equals("/")) {
            return DEFAULT_DIRECTORY + DEFAULT_PATH + HTML_EXTENSION;
        }
        return DEFAULT_DIRECTORY + name + HTML_EXTENSION;
    }

    public String getName() {
        return name;
    }
}
