package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpBody;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.util.parser.BodyParserFactory;
import org.apache.util.parser.Parser;

public class HttpRequest {

    private static final int OFFSET = 0;

    private final HttpRequestLine startLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(HttpRequestLine startLine, HttpHeaders headers, HttpBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        HttpRequestLine httpRequestLine = new HttpRequestLine(parseStartLine(reader));
        HttpHeaders httpHeaders = new HttpHeaders(parseHeaders(reader));
        HttpBody httpBody = new HttpBody(parseBody(reader, httpHeaders.getContentLength()));

        return new HttpRequest(httpRequestLine, httpHeaders, httpBody);
    }

    private static String parseStartLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private static String parseHeaders(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while (StringUtils.isNoneBlank(line = reader.readLine())) {
            builder.append(line).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String parseBody(BufferedReader reader, int countLength) throws IOException {
        char[] buffer = new char[countLength];
        reader.read(buffer, OFFSET, countLength);
        return new String(buffer);
    }

    public String getBody() {
        return body.getBody();
    }

    public Map<String, String> getKeyValueBodies() {
        ContentType contentType = this.getContentType();
        Parser parser = BodyParserFactory.getParser(contentType);

        return parser.parse(this.getBody());
    }

    public String getPath() {
        return startLine.getUri();
    }

    public HttpCookies getCookies() {
        return headers.getCookies();
    }

    public ContentType getContentType() {
        return headers.getContentType();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }
}
