package org.apache.coyote.http11.request;

import java.net.URI;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.HttpMethod;
import org.apache.coyote.http11.file.FileDetails;
import org.apache.coyote.http11.request.parser.HttpRequestUriParser;

public class HttpRequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_LINE_COUNT = 3;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final HttpRequestUri requestUri;
    private final String httpVersion;

    private HttpRequestLine(HttpMethod httpMethod, HttpRequestUri requestUri, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine from(String input) {
        validateNotBlank(input);
        String[] splitLine = input.split(REQUEST_LINE_DELIMITER);
        validateLineSize(splitLine);
        return new HttpRequestLine(
                HttpMethod.valueOf(splitLine[HTTP_METHOD_INDEX]),
                HttpRequestUriParser.parse(splitLine[REQUEST_URI_INDEX]),
                splitLine[HTTP_VERSION_INDEX]
        );
    }

    private static void validateNotBlank(String input) {
        if (StringUtils.isBlank(input)) {
            throw new UncheckedHttpException(new IllegalArgumentException("Http Request Line은 비어있을 수 없습니다."));
        }
    }

    private static void validateLineSize(String[] splitLine) {
        if (splitLine.length != REQUEST_LINE_COUNT) {
            throw new UncheckedHttpException(new IllegalArgumentException("Http Request Line 형식이 잘못 되었습니다."));
        }
    }

    public FileDetails getFileDetails() {
        return requestUri.getFileDetails();
    }

    public Map<String, String> getQueryParams() {
        return requestUri.queryParams();
    }

    public URI getUri() {
        return requestUri.uri();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
