package org.apache.coyote.http11;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.text.html.Option;

import static java.util.Optional.ofNullable;
import static org.apache.coyote.http11.HttpMethod.POST;

public class HttpRequest {

    private HttpMethod method;
    private HttpUri uri;
    private Map<String, String> queryParameters;
    private String messageBody;
    private Headers headers;
    private Cookies cookies;

    public HttpRequest(final HttpMethod method, final HttpUri uri, final Map<String, String> queryParameters,
                       final Map<String, String> headers, final String messageBody, final Map<String, String> cookie) {
        this.method = method;
        this.uri = uri;
        this.queryParameters = ofNullable(queryParameters).orElse(Map.of());
        this.headers = ofNullable(headers).map(Headers::new).orElse(new Headers());
        this.messageBody = Optional.ofNullable(messageBody).orElse("");
        this.cookies = new Cookies(cookie);
    }

    public boolean isPost() {
        return method == POST;
    }

    public HttpUri getUri() {
        return uri;
    }

    public Map<String, String> getForm() {
        String[] split = messageBody.split("&");
        return Arrays.asList(split)
                .stream()
                .map(s -> s.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    public String getCookie(String key) {
        return cookies.getCookie(key);
    }

}
