package http;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequestLine {

    private static final String ROOT_URI = "/";
    private static final String REQUEST_DELIMITER = " ";

    private final HttpMethod method;
    private final URI uri;
    private final String version;

    public HttpRequestLine(String requestLine) {
        try {
            String[] tokens = requestLine.split(REQUEST_DELIMITER);
            validateTokensLength(tokens);
            this.method = HttpMethod.nameOf(tokens[0]);
            this.uri = new URI(tokens[1]);
            this.version = tokens[2];
        } catch (URISyntaxException e) {
            throw new RuntimeException("프로토콜이 안맞아요!!!");
        }
    }

    private void validateTokensLength(String[] tokens) {
        if (tokens.length != 3) {
            throw new RuntimeException("failed to parse request part");
        }
    }

    public boolean isRootUri() {
        return uri.getPath().equals(ROOT_URI);
    }

    public URI getUri() {
        return uri;
    }
}
