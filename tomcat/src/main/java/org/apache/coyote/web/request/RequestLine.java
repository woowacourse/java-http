package org.apache.coyote.web.request;

import java.util.Optional;
import org.apache.coyote.exception.HttpRequestStartLineNotValidException;
import org.apache.coyote.support.HttpMethod;

public class RequestLine {

    private static final int REQUEST_LINE_LENGTH = 3;
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";
    private static final int NO_SPLIT_SIGN = 1;
    private static final String EXTENSION_DELIMITER = "\\.";

    private final HttpMethod method;
    private final String requestUrl;
    private final String queryParameter;
    private final String version;

    private RequestLine(final HttpMethod method,
                        final String requestUrl,
                        final String queryParameter,
                        final String version) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.queryParameter = queryParameter;
        this.version = version;
    }

    public static RequestLine from(final String[] requestLine) {
        if (requestLine.length != REQUEST_LINE_LENGTH) {
            throw new HttpRequestStartLineNotValidException();
        }
        String[] splitRequestUrl = requestLine[1].split(QUERY_PARAMETER_DELIMITER);
        if (splitRequestUrl.length == NO_SPLIT_SIGN) {
            return new RequestLine(HttpMethod.of(requestLine[0]),
                    splitRequestUrl[0],
                    null,
                    requestLine[2]);
        }
        return new RequestLine(HttpMethod.of(requestLine[0]),
                splitRequestUrl[0],
                splitRequestUrl[1],
                requestLine[2]);
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
        return Optional.of(split[1]);
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
