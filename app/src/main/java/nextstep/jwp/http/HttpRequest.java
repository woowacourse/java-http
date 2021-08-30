package nextstep.jwp.http;

import java.util.Map;

public class HttpRequest {

    private String httpMethod;
    private String uri;
    private String httpVersion;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private String body;

    public HttpRequest(){

    }

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

    public String getParameterValues(String key) {
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

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
