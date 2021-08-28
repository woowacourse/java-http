package nextstep.jwp.request;

public class RequestUri {

    private static final String MAIN_PATH = "/";
    private static final String DEFAULT_FILE_PATH = "static/";
    private static final String DEFAULT_FILE = "index.html";

    private final String uri;

    public RequestUri(String uri) {
        this.uri = uri;
    }

    public FileName toFileName() {
        if (MAIN_PATH.equals(uri)) {
            return new FileName(DEFAULT_FILE_PATH + DEFAULT_FILE);
        }
        return new RequestPath(uri.substring(1)).toFileName();
    }

    public RequestPath toPath() {
        return new RequestPath(uri.substring(1));
    }

    public String getUri() {
        return uri;
    }
}
