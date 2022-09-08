package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.coyote.http11.Protocol;
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

    private final HttpMethod method;
    private final Protocol version;
    private final URL requestURL;
    private final QueryParams params;
    private final RequestHeaders headers;
    private final RequestBody body;

    public Request(final HttpMethod method, final Protocol version, final URL requestURL, final QueryParams params, final RequestHeaders headers,
                   final RequestBody body) {
        this.method = method;
        this.version = version;
        this.requestURL = requestURL;
        this.params = params;
        this.headers = headers;
        this.body = body;
    }

    public static Request of(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String startLine = bufferedReader.readLine();
        final String[] splitStartLine = startLine.split(SPACE_DELIMITER);
        final HttpMethod httpMethod = HttpMethod.of(splitStartLine[METHOD_INDEX]);
        final URL url = URL.of(splitStartLine[URI_INDEX]);
        final QueryParams params = parseQueryParams(splitStartLine[URI_INDEX]);
        final Protocol protocol = Protocol.of(splitStartLine[VERSION_INDEX]);
        final RequestHeaders headers = parseHeader(bufferedReader);
        final int contentLength = headers.getContentLength();
        final RequestBody requestBody = parseBody(bufferedReader, contentLength);
        return new Request(httpMethod, protocol, url, params, headers, requestBody);
    }

    private static QueryParams parseQueryParams(final String parsedUrl) {
        if (!parsedUrl.contains(QUERY_PARAM_DELIMITER)) {
            return QueryParams.ofEmpty();
        }
        final String urlQueryParams = parsedUrl.split(QUERY_PARAM_DELIMITER_REGEX)[PARAM_INDEX];
        return QueryParams.of(urlQueryParams);
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
        return requestURL.isForStaticFile();
    }

    public boolean isDefaultUrl() {
        return requestURL.isDefault();
    }

    public boolean hasPath(final String path) {
        return this.requestURL.hasPath(path);
    }

    public String findJsessionid() {
        return headers.getJsessionid();
    }

    public boolean hasJsessionid() {
        return headers.hasJsessionid();
    }
    public HttpMethod getMethod() {
        return method;
    }

    public URL getURL() {
        return requestURL;
    }

    public QueryParams getParams() {
        return params;
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return body;
    }
}
