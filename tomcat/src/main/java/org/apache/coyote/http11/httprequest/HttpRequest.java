package org.apache.coyote.http11.httprequest;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpPath;
import org.apache.coyote.http11.HttpVersion;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.coyote.http11.HttpMethod.POST;
import static org.apache.coyote.http11.HttpVersion.HTTP_1_1;

public class HttpRequest {

    public static final String QUERY_PARAM_DELIMITER = "&";
    public static final String EMPTY = "";
    private final HttpMethod method;
    private final HttpPath path;
    private final HttpVersion version;
    private final Map<String, String> queryParameters;
    private final String messageBody;
    private final Headers headers;
    private final HttpCookie httpCookie;

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public HttpRequest(final HttpMethod method, final HttpPath path, final HttpVersion httpVersion,
                       final Map<String, String> queryParameters,
                       final Map<String, String> headers, final String messageBody, final Map<String, String> cookie) {
        this.method = method;
        this.path = path;
        this.version = ofNullable(httpVersion).orElse(HTTP_1_1);
        this.queryParameters = ofNullable(queryParameters).orElse(Map.of());
        this.headers = ofNullable(headers).map(Headers::new).orElse(new Headers());
        this.messageBody = Optional.ofNullable(messageBody).orElse(EMPTY);
        this.httpCookie = ofNullable(cookie).map(HttpCookie::new).orElse(new HttpCookie(Map.of()));
    }

    public boolean isPost() {
        return method == POST;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpPath getPath() {
        return path;
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
