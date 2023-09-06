package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.coyote.http11.HttpMethod.POST;

public class HttpRequest {

    public static final String QUERY_PARAM_DELIMITER = "&";
    public static final String EMPTY = "";
    private HttpMethod method;
    private HttpUri uri;
    private Map<String, String> queryParameters;
    private String messageBody;
    private Headers headers;
    private HttpCookie httpCookie;

    public HttpRequest(final HttpMethod method, final HttpUri uri, final Map<String, String> queryParameters,
                       final Map<String, String> headers, final String messageBody, final Map<String, String> cookie) {
        this.method = method;
        this.uri = uri;
        this.queryParameters = ofNullable(queryParameters).orElse(Map.of());
        this.headers = ofNullable(headers).map(Headers::new).orElse(new Headers());
        this.messageBody = Optional.ofNullable(messageBody).orElse(EMPTY);
        this.httpCookie = new HttpCookie(cookie);
    }

    public boolean isPost() {
        return method == POST;
    }

    public HttpUri getUri() {
        return uri;
    }

    public Map<String, String> getForm() {
        String[] keyValues = messageBody.split(QUERY_PARAM_DELIMITER);
        return Arrays.asList(keyValues).stream()
                .map(keyValue -> keyValue.split("="))
                .collect(toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
    }

    public String getCookie(String key) {
        return httpCookie.getCookie(key);
    }

}
