package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.coyote.HttpMethod;

public class Request {
    protected static final String SPACE_DELIMITER = " ";
    protected static final int URI_INDEX = 1;
    protected static final String QUERY_PARAM_DELIMITER = "?";
    protected static final String QUERY_PARAM_DELIMITER_REGEX = "\\?";
    protected static final int PARAM_INDEX = 1;
    protected static final int PATH_INDEX = 0;
    protected static final String HEADER_KEY_VALUE_DELIMITER = ":";
    protected static final int KEY = 0;
    protected static final int VALUE = 1;

    private final HttpMethod method;
    private final URL requestURL;
    private final QueryParams params;
    private final Headers headers;
    private final RequestBody body;

    public Request(final HttpMethod method, final URL requestURL, final QueryParams params, final Headers headers,
                   final RequestBody body) {
        this.method = method;
        this.requestURL = requestURL;
        this.params = params;
        this.headers = headers;
        this.body = body;
    }

    public static Request of(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String startLine = bufferedReader.readLine();
        final String[] splitStartLine = startLine.split(SPACE_DELIMITER);
        final HttpMethod httpMethod = HttpMethod.of(splitStartLine[KEY]);
        final URL url = URL.of(splitStartLine[URI_INDEX]);
        final QueryParams params = parseQueryParams(splitStartLine[URI_INDEX]);
        final Headers headers = parseHeader(bufferedReader);
        final int contentLength = headers.getContentLength();
        final RequestBody requestBody = parseBody(bufferedReader, contentLength);
        return new Request(httpMethod, url, params, headers, requestBody);
    }

    private static QueryParams parseQueryParams(final String parsedUrl) {
        if (!parsedUrl.contains(QUERY_PARAM_DELIMITER)) {
            return QueryParams.ofEmpty();
        }
        final String urlQueryParams = parsedUrl.split(QUERY_PARAM_DELIMITER_REGEX)[PARAM_INDEX];
        return QueryParams.of(urlQueryParams);
    }

    private static Headers parseHeader(final BufferedReader bufferedReader) throws IOException {
        bufferedReader.readLine();
        String headerKeyValue = bufferedReader.readLine();
        final HashMap<String, String> headers = new HashMap<>();
        while (!headerKeyValue.isBlank()) {
            final String[] splitHeaderKeyValue = headerKeyValue.split(HEADER_KEY_VALUE_DELIMITER);
            headers.put(splitHeaderKeyValue[KEY], splitHeaderKeyValue[VALUE]);
            headerKeyValue = bufferedReader.readLine();
        }
        return new Headers(headers);
    }

    private static RequestBody parseBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        final char[] chars = new char[contentLength];
        bufferedReader.read(chars, 0, contentLength);
        return new RequestBody(String.valueOf(chars));
    }

    public boolean isForStaticFile() {
        return requestURL.isForStaticFile();
    }

    public boolean isDefaultUrl() {
        return requestURL.isDefault();
    }

    public boolean hasPath(final String path) {
        return this.requestURL.hasPath(path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URL getRequestURL() {
        return requestURL;
    }

    public QueryParams getParams() {
        return params;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return body;
    }
}
