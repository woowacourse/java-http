package nextstep.jwp;

import java.util.Map;

public class HttpRequest {

    private final String httpMethod;
    private final String uri;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String body;

    public HttpRequest(String httpMethod, String uri, String httpVersion,
        Map<String, String> headers, Map<String, String> parameters, String body) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.parameters = parameters;
        this.body = body;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getParameterValues(String key){
        return parameters.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getBody() {
        return body;
    }
}
