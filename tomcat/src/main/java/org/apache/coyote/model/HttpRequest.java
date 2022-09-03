package org.apache.coyote.model;

import nextstep.jwp.exception.InvalidRequestFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.utils.RequestUtil.calculatePath;
import static org.apache.coyote.utils.RequestUtil.getExtension;
import static org.apache.coyote.utils.RequestUtil.getParam;

public class HttpRequest {

    public static final int REQUEST_SIZE_DEADLINE = 3;
    private final String uri;
    private final String path;
    private final Map<String, String> params;
    private final String contentType;
    private final HttpMethod httpMethod;

    protected HttpRequest(String uri, String path, Map<String, String> params, String contentType, HttpMethod httpMethod) {
        this.uri = uri;
        this.path = path;
        this.params = params;
        this.contentType = contentType;
        this.httpMethod = httpMethod;
    }

    public static HttpRequest of(final String requestLine) {
        List<String> requests = Arrays.asList(Objects.requireNonNull(requestLine).split(" "));
        validateRequestSize(requests);
        final var uri = requests.get(1);
        final var path = calculatePath(uri);
        final var params = getParam(uri);
        final var content = Content.getType(getExtension(path));
        return new HttpRequest(uri, path, params, content, HttpMethod.of(requests.get(0)));
    }

    private static void validateRequestSize(List<String> requests) {
        if (requests.size() < REQUEST_SIZE_DEADLINE) {
            throw new InvalidRequestFormat("요청 값이 올바르지 않습니다.");
        }
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getContentType() {
        return contentType;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
