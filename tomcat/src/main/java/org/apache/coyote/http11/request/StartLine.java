package org.apache.coyote.http11.request;

public class StartLine {
    private final HttpMethod method;
    private final RequestUri uri;
    private final HttpVersion version;

    private StartLine(final HttpMethod method, final RequestUri uri, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static StartLine create(final String line) {
        String[] targets = line.split(" ");
        return new StartLine(HttpMethod.valueOf(targets[0]), RequestUri.create(targets[1]),
                HttpVersion.fromDetail(targets[2]));
    }

    public String getUri() {
        return uri.getUri();
    }
}
