package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String START_LINE = "START_LINE";
    private static final String HEADERS_KEY_VALUE_SEPARATOR = ": ";
    private static final int KEY_ON_KEY_VALUE_FORMAT = 0;
    private static final int VALUE_ON_KEY_VALUE_FORMAT = 1;

    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(HttpRequestHeader httpRequestHeader, HttpRequestBody httpRequestBody) {
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest readFromInputStream(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final Map<String, String> headerLines = new HashMap<>();
        fillHeaderPart(reader, headerLines);
        final HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(headerLines);
        final String body = extractBody(reader, httpRequestHeader);
        final HttpRequestBody httpRequestBody = new HttpRequestBody(body);
        return new HttpRequest(httpRequestHeader, httpRequestBody);
    }

    private static void fillHeaderPart(BufferedReader reader, Map<String, String> headerLines)
        throws IOException {
        String startLine = reader.readLine();
        headerLines.put(START_LINE, startLine);
        while (reader.ready()) {
            final String oneLine = reader.readLine();
            if ("".equals(oneLine) || oneLine == null) {
                break;
            }
            final String[] part = oneLine.split(HEADERS_KEY_VALUE_SEPARATOR);
            headerLines.put(part[KEY_ON_KEY_VALUE_FORMAT], part[VALUE_ON_KEY_VALUE_FORMAT]);
        }
    }

    private static String extractBody(BufferedReader reader, HttpRequestHeader httpRequestHeader)
        throws IOException {
        try {
            int bodyLength = httpRequestHeader.contentLength();
            char[] buffer = new char[bodyLength];
            reader.read(buffer, 0, bodyLength);
            return new String(buffer);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    public String method() {
        return httpRequestHeader.method();
    }

    public String getRequestURLWithoutQuery() {
        return httpRequestHeader.getRequestURLWithoutQuery();
    }

    public Map<String, String> parsedBody() {
        return httpRequestBody.parsedBody();
    }

    public boolean isResource() {
        return httpRequestHeader.isResource();
    }

    public String resourceType() {
        return httpRequestHeader.resourceType();
    }

    public HttpRequestHeader getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }
}
