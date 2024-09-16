package jakarta.http;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;

public class HttpRequest {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String DEFAULT_JSSESION_ID = "";
    private static final int METHOD_POSITION = 0;
    private static final int PATH_POSITION = 1;

    private final HttpMethod httpMethod;
    private final URI uri;
    private final HttpVersion httpVersion;
    private final Header header;
    private final HttpBody httpBody;
    private final HttpSessionWrapper httpSessionWrapper;

    private String sessionId;

    public HttpRequest(
            HttpMethod httpMethod,
            URI uri,
            HttpVersion httpVersion,
            Header header,
            HttpBody httpBody,
            HttpSessionWrapper httpSessionWrapper,
            String sessionId
    ) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.header = header;
        this.httpBody = httpBody;
        this.httpSessionWrapper = httpSessionWrapper;
        this.sessionId = sessionId;
    }

    public static HttpRequest createHttpRequest(
            String requestLine,
            Header header,
            HttpBody body,
            HttpVersion version,
            HttpSessionWrapper manager
    ) {
        String[] requestLines = requestLine.split(REQUEST_LINE_DELIMITER);
        HttpMethod httpMethod = HttpMethod.from(requestLines[METHOD_POSITION]);
        URI uri = URI.create(requestLines[PATH_POSITION]);

        return new HttpRequest(
                httpMethod,
                uri,
                version,
                header,
                body,
                manager,
                header.getJSessionId().orElse(DEFAULT_JSSESION_ID)
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    public String getPath() {
        return uri.getPath();
    }

    public QueryParameter getQueryParameter() {
        return new QueryParameter(uri.getQuery());
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public Header getHeader() {
        return header;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    public HttpSession getSession(boolean sessionCreateIfAbsent) throws IOException {
        HttpSession session = httpSessionWrapper.getSession(sessionCreateIfAbsent, sessionId);
        sessionId = session.getId();

        return session;
    }
}
