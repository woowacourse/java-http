package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.common.HttpHeader;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader headers;
    private RequestBody body;

    public HttpRequest(RequestLine requestLine, HttpHeader headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    // TODO: refactor
    public Map<String, String> parseFormBody() {
        Map<String, String> result = new HashMap<>();
        if (body == null) {
            return result;
        }
        String[] params = body.body().split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    public Optional<String> getFormValue(String key) {
        Map<String, String> formBody = parseFormBody();
        if (!formBody.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(formBody.get(key));
    }

    public HttpMethod getMethod() {
        return requestLine.method();
    }

    public String getPath() {
        return requestLine.path();
    }

    public String getVersion() {
        return requestLine.version();
    }

    public Map<String, String> getHeaders() {
        return headers.headers();
    }

    public String getSessionId() {
        return headers.getSessionId().orElse("");
    }

    public String getBody() {
        return body.body();
    }

    public void setBody(String body) {
        this.body = new RequestBody(body);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "body=" + body +
                ", requestLine=" + requestLine +
                ", header=" + headers +
                '}';
    }
}
