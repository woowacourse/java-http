package nextstep.jwp.http.request;

public class RequestUri {

    private static final String EXTENSION = ".";
    private static final String QUERY_MARK = "?";

    private final String uri;

    public RequestUri(String uri) {
        this.uri = uri;
    }

    public String getPath() {
        // e.g. /login.html -> /login
        if (uri.contains(EXTENSION)) {
            return uri.substring(0, uri.indexOf(EXTENSION));
        }
        // e.g. /login?account=me || /login.html?account=me -> /login
        if (uri.contains(QUERY_MARK)) {
            return uri.substring(0, uri.indexOf(QUERY_MARK));
        }
        // e.g. / || /login
        return uri;
    }

    public String getUri() {
        return uri;
    }
}
