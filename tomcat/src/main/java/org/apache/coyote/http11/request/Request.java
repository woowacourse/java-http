package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.coyote.http11.URL;

public class Request {
    public static final String SPACE_DELIMITER = " ";
    public static final int METHOD_INDEX = 0;
    public static final int URI_INDEX = 1;
    public static final String QUERY_PARAM_DELIMITER = "?";
    public static final String QUERY_PARAM_DELIMITER_REGEX = "\\?";
    public static final int PARAM_INDEX = 1;
    public static final int PATH_INDEX = 0;
    public static final String HEADER_KEY_VALUE_DELIMITER = ":";
    public static final int KEY = 0;
    public static final int VALUE = 1;
    private static final int VERSION_INDEX = 2;

    private final StartLine startLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    private Request(final StartLine startLine, final RequestHeaders headers,
                   final RequestBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Request of(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String startLine = bufferedReader.readLine();
        final StartLine startLine1 = StartLine.of(startLine);
        final RequestHeaders headers = parseHeader(bufferedReader);
        final int contentLength = headers.getContentLength();
        final RequestBody requestBody = parseBody(bufferedReader, contentLength);
        return new Request(startLine1, headers, requestBody);
    }

    private static RequestHeaders parseHeader(final BufferedReader bufferedReader) throws IOException {
        String headerKeyValue = bufferedReader.readLine();
        final HashMap<String, String> headers = new HashMap<>();
        while (!headerKeyValue.isBlank()) {
            final String[] splitHeaderKeyValue = headerKeyValue.split(HEADER_KEY_VALUE_DELIMITER);
            headers.put(splitHeaderKeyValue[KEY], splitHeaderKeyValue[VALUE]);
            headerKeyValue = bufferedReader.readLine();
        }
        return new RequestHeaders(headers);
    }

    private static RequestBody parseBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        final char[] body = new char[contentLength];
        bufferedReader.read(body);
        return new RequestBody(String.valueOf(body));
    }

    public boolean isForStaticFile() {
        return startLine.isForStaticFile();
    }

    public boolean isDefaultUrl() {
        return startLine.isDefault();
    }

    public boolean hasPath(final String path) {
        return startLine.hasPath(path);
    }

    public boolean hasJsessionid() {
        return headers.hasJsessionid();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public URL getURL() {
        return startLine.getURL();
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return body;
    }
}
