package nextstep.jwp;

public class RequestLine {

    private final Method method;
    private final String uri;
    private final String protocolVersion;

    public RequestLine(String method, String uri, String protocolVersion) {
        this(Method.of(method), uri, protocolVersion);
    }

    public RequestLine(Method method, String uri, String protocolVersion) {
        this.method = method;
        this.uri = uri;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(String line) {
        final String[] firstLineElements = line.split(" ");
        final String httpMethod = firstLineElements[0];
        final String uri = firstLineElements[1];
        final String protocolVersion = firstLineElements[2];

        return new RequestLine(httpMethod, uri, protocolVersion);
    }

    public String toPath() {
        final int index = uri.indexOf("?");
        return uri.substring(0, index);
    }

    public String toQueryString() {
        final int index = uri.indexOf("?");
        return uri.substring(index + 1);
    }

    public String toResource() {
        return uri.substring(1);
    }
}
