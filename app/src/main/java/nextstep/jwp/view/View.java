package nextstep.jwp.view;

public class View {

    private static final String DEFAULT_DIRECTORY = "static";
    private static final String HTML_EXTENSION = ".html";

    private final String name;

    public View(String name) {
        this.name = name;
    }

    public ViewResolver resolve() {
        if (name.contains(".")) {
            return new ViewResolver(DEFAULT_DIRECTORY + name);
        }
        if (name.equals("/")) {
            return new ViewResolver(DEFAULT_DIRECTORY + "/index" + HTML_EXTENSION);
        }
        return new ViewResolver(DEFAULT_DIRECTORY + name + HTML_EXTENSION);
    }

    public String getName() {
        return name;
    }
}
