package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.coyote.HttpMethod;

public class Http11Request {
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
    private final Http11URL requestURL;
    private final Http11QueryParams params;
    private final Http11Headers headers;
    private final Http11RequestBody body;

    public Http11Request(final HttpMethod method, final Http11URL requestURL, final Http11QueryParams params, final Http11Headers headers,
                         final Http11RequestBody body) {
        this.method = method;
        this.requestURL = requestURL;
        this.params = params;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Request of(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String startLine = bufferedReader.readLine();
        final String[] splitStartLine = startLine.split(SPACE_DELIMITER);
        final HttpMethod httpMethod = HttpMethod.of(splitStartLine[KEY]);
        final Http11URL http11URL = Http11URL.of(splitStartLine[URI_INDEX]);
        final Http11QueryParams params = parseQueryParams(splitStartLine[URI_INDEX]);
        final Http11Headers headers = parseHeader(bufferedReader);
        final int contentLength = headers.getContentLength();
        final Http11RequestBody requestBody = parseBody(bufferedReader, contentLength);
        return new Http11Request(httpMethod, http11URL, params, headers, requestBody);
    }

    private static Http11QueryParams parseQueryParams(final String parsedUrl) {
        if (!parsedUrl.contains(QUERY_PARAM_DELIMITER)) {
            return Http11QueryParams.ofEmpty();
        }
        final String urlQueryParams = parsedUrl.split(QUERY_PARAM_DELIMITER_REGEX)[PARAM_INDEX];
        return Http11QueryParams.of(urlQueryParams);
    }

    private static Http11Headers parseHeader(final BufferedReader bufferedReader) throws IOException {
        bufferedReader.readLine();
        String headerKeyValue = bufferedReader.readLine();
        final HashMap<String, String> headers = new HashMap<>();
        while (!headerKeyValue.isBlank()) {
            final String[] splitHeaderKeyValue = headerKeyValue.split(HEADER_KEY_VALUE_DELIMITER);
            headers.put(splitHeaderKeyValue[KEY], splitHeaderKeyValue[VALUE]);
            headerKeyValue = bufferedReader.readLine();
        }
        return new Http11Headers(headers);
    }

    private static Http11RequestBody parseBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        final char[] chars = new char[contentLength];
        bufferedReader.read(chars, 0, contentLength);
        return new Http11RequestBody(String.valueOf(chars));
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

    public Http11URL getRequestURL() {
        return requestURL;
    }

    public Http11QueryParams getParams() {
        return params;
    }

    public Http11Headers getHeaders() {
        return headers;
    }

    public Http11RequestBody getBody() {
        return body;
    }
}
