package org.apache.coyote.web.request;

import static org.apache.coyote.web.session.SessionManager.JSESSIONID;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.support.HttpHeader;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpMethod;

public class Request {

    private static final String DEFAULT_REQUEST_EXTENSION = "strings";
    private static final String CONNECT_DELIMITER = "&";
    private static final String ASSIGN_DELIMITER = "=";
    private static final String SET_COOKIE_DELIMITER = "; ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public Request(final RequestLine requestLine,
                   final HttpHeaders httpHeaders,
                   final String requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public boolean isFileRequest() {
        return requestLine.getFileExtension().isPresent();
    }

    public String getRequestExtension() {
        Optional<String> fileExtension = requestLine.getFileExtension();
        if (fileExtension.isEmpty()) {
            return DEFAULT_REQUEST_EXTENSION;
        }
        return fileExtension.get();
    }

    public Map<String, String> getQueryParameters() {
        String queryParameter = requestLine.getQueryParameter();
        if (Objects.isNull(queryParameter)) {
            return Collections.emptyMap();
        }
        return doParse(queryParameter);
    }

    public boolean isSameHttpMethod(final HttpMethod method) {
        return requestLine.isSameMethod(method);
    }

    public boolean isSameRequestUrl(final String url) {
        return requestLine.isSameUrl(url);
    }

    public Map<String, String> parseBody() {
        return doParse(requestBody);
    }

    private Map<String, String> doParse(final String content) {
        return Arrays.stream(content.split(CONNECT_DELIMITER))
                .collect(Collectors.toMap(parameter -> getSplitValue(parameter, ASSIGN_DELIMITER, KEY_INDEX),
                        parameter -> getSplitValue(parameter, ASSIGN_DELIMITER, VALUE_INDEX)));
    }

    private String getSplitValue(final String content, final String delimiter, final int index) {
        return content.split(delimiter)[index];
    }

    public Optional<String> getSession() {
        String cookie = httpHeaders.getValueOrDefault(HttpHeader.COOKIE.getValue(), "");
        return Arrays.stream(cookie.split(SET_COOKIE_DELIMITER))
                .filter(value -> value.contains(JSESSIONID))
                .map(session -> session.split(ASSIGN_DELIMITER)[1])
                .findFirst();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
