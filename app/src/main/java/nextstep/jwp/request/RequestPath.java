package nextstep.jwp.request;

public class RequestPath {

    private static final String DEFAULT_PATH = "static/";
    private static final String FILE_EXTENSION = ".html";

    private final String path;

    public RequestPath(String path) {
        this.path = path;
    }

    public FileName toFileName() {
        if (path.contains(".")) {
            return new FileName(DEFAULT_PATH + path);
        }
        return new FileName(DEFAULT_PATH + path + FILE_EXTENSION);
    }

    public String getPath() {
        return path;
    }
}
