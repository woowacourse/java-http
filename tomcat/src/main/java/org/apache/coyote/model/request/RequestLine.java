package org.apache.coyote.model.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.utils.RequestUtil.calculatePath;
import static org.apache.coyote.utils.RequestUtil.getExtension;
import static org.apache.coyote.utils.RequestUtil.getParam;

public class RequestLine {

    public static final int METHOD_INDEX = 0;
    public static final int PARAM_INDEX = 1;
    private final Method method;
    private final Map<String, String> queryParams;
    private final String path;
    private final String contentType;

    private RequestLine(final Method method, final Map<String, String> params, final String path, final String contentType) {
        this.method = method;
        this.queryParams = params;
        this.path = path;
        this.contentType = contentType;
    }

    public static RequestLine of(final String readLine) {
        final List<String> requests = Arrays.asList(Objects.requireNonNull(readLine).split(" "));
        final var method = Method.of(requests.get(METHOD_INDEX));
        final var httpParams = getParam(requests.get(PARAM_INDEX));
        final var path = calculatePath(requests.get(PARAM_INDEX));
        final var contentType = ContentType.getType(getExtension(path));

        return new RequestLine(method, httpParams, path, contentType);
    }

    public boolean checkMethod(final Method method) {
        return this.method.equals(method);
    }

    public Method getMethod() {
        return method;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getPath() {
        return path;
    }
}
