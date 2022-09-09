package nextstep.jwp.view;

import org.apache.coyote.util.FileReader;

public enum View {

    INDEX("index.html"),
    LOGIN("login.html"),
    REGISTER("register.html"),

    UNAUTHORIZED("401.html"),
    NOT_FOUND("404.html"),
    ;

    private final String viewFileName;

    View(String viewFileName) {
        this.viewFileName = viewFileName;
    }

    public String getContents() {
        return FileReader.readStaticFile(viewFileName, this.getClass());
    }

    public String getViewFileName() {
        return viewFileName;
    }
}
