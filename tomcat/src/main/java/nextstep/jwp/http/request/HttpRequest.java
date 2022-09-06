package nextstep.jwp.http.request;

import static nextstep.jwp.http.common.SessionManager.JSESSIONID;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.http.common.HttpHeaders;

public class HttpRequest {

    private static final String COOKIES_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final RequestLine requestLine;
    private final HttpHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine,
                       final HttpHeaders requestHeaders,
                       final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public String getRequestMethod() {
        return requestLine.getRequestMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public String getRequestExtension() {
        return requestLine.getRequestExtension();
    }

    public Optional<String> getJSessionValue() {
        if (!requestHeaders.isExistCookie()) {
            return Optional.empty();
        }
        return Arrays.stream(requestHeaders.getHeader(HttpHeaders.COOKIE).split(COOKIES_DELIMITER))
            .filter(cookie -> cookie.split(COOKIE_DELIMITER)[KEY_INDEX].equals(JSESSIONID))
            .map(cookie -> cookie.split(COOKIE_DELIMITER)[VALUE_INDEX])
            .findAny();
    }

    public String getQueryParameterValue(final String parameter) {
        return requestLine.getQueryParameterValue(parameter);
    }

    public Map<String, String> getQueryParameters() {
        return requestLine.getQueryParameters();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
