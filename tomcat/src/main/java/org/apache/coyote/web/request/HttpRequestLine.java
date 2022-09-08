package org.apache.coyote.web.request;

import java.util.Optional;
import org.apache.coyote.exception.HttpRequestStartLineNotValidException;
import org.apache.coyote.support.HttpMethod;

public class HttpRequestLine {

    private static final int REQUEST_LINE_LENGTH = 3;
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";
    private static final int NO_SPLIT_SIGN = 1;
    private static final String EXTENSION_DELIMITER = "\\.";
    private static final int REQUEST_URL_INDEX = 1;
    private static final int REQUEST_URL_ONLY_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int FILE_EXTENSION_INDEX = 1;


    private final HttpMethod method;
    private final String requestUrl;
    private final String queryParameter;
    private final String version;

    private HttpRequestLine(final HttpMethod method,
                            final String requestUrl,
                            final String queryParameter,
                            final String version) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.queryParameter = queryParameter;
        this.version = version;
    }

    public static HttpRequestLine from(final String[] requestLine) {
        if (requestLine.length != REQUEST_LINE_LENGTH) {
            throw new HttpRequestStartLineNotValidException();
        }
        String[] splitRequestUrl = requestLine[REQUEST_URL_INDEX].split(QUERY_PARAMETER_DELIMITER);
        if (splitRequestUrl.length == NO_SPLIT_SIGN) {
            return new HttpRequestLine(HttpMethod.of(requestLine[HTTP_METHOD_INDEX]),
                    splitRequestUrl[REQUEST_URL_ONLY_INDEX],
                    null,
                    requestLine[HTTP_VERSION_INDEX]);
        }
        return new HttpRequestLine(HttpMethod.of(requestLine[HTTP_METHOD_INDEX]),
                splitRequestUrl[REQUEST_URL_ONLY_INDEX],
                splitRequestUrl[QUERY_STRING_INDEX],
                requestLine[HTTP_VERSION_INDEX]);
    }

    public boolean isSameMethod(final HttpMethod method) {
        return this.method.isSameMethod(method);
    }

    public boolean isSameUrl(final String url) {
        return requestUrl.equals(url);
    }

    public Optional<String> getFileExtension() {
        String[] split = requestUrl.split(EXTENSION_DELIMITER);
        if (split.length == NO_SPLIT_SIGN) {
            return Optional.empty();
        }
        return Optional.of(split[FILE_EXTENSION_INDEX]);
    }

    public boolean hasQueryParameter() {
        return queryParameter != null;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getQueryParameter() {
        return queryParameter;
    }

    public String getVersion() {
        return version;
    }
}
