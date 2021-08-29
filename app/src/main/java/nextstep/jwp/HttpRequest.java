package nextstep.jwp;

import java.util.Map;

public class HttpRequest {

    private final String httpMethod;
    private final String uri;
    private final String httpVersion;
    private final Map<String, String> parameters;

    public HttpRequest(String httpMethod, String uri, String httpVersion,
        Map<String, String> parameter) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.parameters = parameter;
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
}
