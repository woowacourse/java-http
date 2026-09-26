package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final Parameters parameters;
    private final String sessionId;

    private HttpRequest(RequestLine requestLine, HttpHeaders headers, Parameters parameters, String sessionId) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.parameters = parameters;
        this.sessionId = sessionId;
    }

    public static HttpRequest from(List<String> headLines, String body) {
        RequestLine requestLine = RequestLine.from(headLines.getFirst());
        HttpHeaders headers = HttpHeaders.from(headLines.subList(1, headLines.size()));
        Parameters parameters = Parameters.from(body);

        return new HttpRequest(requestLine, headers, parameters, headers.getSessionId());
    }

    public HttpRequest withSessionId(String sessionId) {
        return new HttpRequest(requestLine, headers, parameters, sessionId);
    }

    public boolean isGet() {
        return requestLine.hasMethod(HttpMethod.GET);
    }

    public boolean isPost() {
        return requestLine.hasMethod(HttpMethod.POST);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getSessionId() {
        return sessionId;
    }
}
