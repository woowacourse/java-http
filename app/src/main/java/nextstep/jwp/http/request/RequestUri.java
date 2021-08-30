package nextstep.jwp.http.request;

public class RequestUri {

    private static final String QUERY_MARK = "?";

    private final String uri;

    public RequestUri(String uri) {
        this.uri = uri;
    }

    public String getPath() {
        if (uri.contains(QUERY_MARK)) {
            return uri.substring(0, uri.indexOf(QUERY_MARK));
        }
        return uri;
    }

    public String getUri() {
        return uri;
    }
}
