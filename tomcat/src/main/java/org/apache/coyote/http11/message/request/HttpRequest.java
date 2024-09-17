package org.apache.coyote.http11.message.request;

import java.net.URI;
import java.util.Map;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpBody;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.util.parser.BodyParserFactory;
import org.apache.util.parser.Parser;

public class HttpRequest {

    private final HttpRequestLine startLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(HttpRequestLine startLine, HttpHeaders headers, HttpBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public Map<String, String> getKeyValueBodies() {
        ContentType contentType = this.getContentType();
        Parser parser = BodyParserFactory.getParser(contentType);

        return parser.parse(this.getBody());
    }

    public ContentType getContentType() {
        return headers.getContentType();
    }

    public String getBody() {
        return body.getBody();
    }

    public URI getUri() {
        return startLine.getUri();
    }

    public boolean hasPath(String path) {
        return startLine.hasPath(path);
    }

    public HttpCookies getCookies() {
        return headers.getCookies();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }
}
