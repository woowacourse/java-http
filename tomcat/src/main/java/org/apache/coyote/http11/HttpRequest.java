package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpRequest {

    private String method;
    private String uri;
    private String messageBody;

    private Map<String, String> headers;

    public static HttpRequest from(List<String> lines) {
        System.out.println(lines);
        final var requestLine = lines.get(0).split(" ");
        return new HttpRequest(requestLine[0], requestLine[1]);
    }

    public HttpRequest(final String method, final String uri) {
        this.method = method;
        this.uri = uri;
    }

    public HttpRequest(final String method, final String uri, final Map<String, String> headers, final String messageBody) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public boolean isGet() {
        return "GET".equals(method);
    }

    public boolean isPost() {
        return "POST".equals(method);
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public Map<String, String> getForm() {
        String[] split = messageBody.split("&");
        return Arrays.asList(split)
                .stream()
                .map(s -> s.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

}
