package org.apache.coyote.model.request;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.apache.coyote.utils.RequestUtil.calculatePath;
import static org.apache.coyote.utils.RequestUtil.getExtension;
import static org.apache.coyote.utils.RequestUtil.getParam;

public class RequestLine {

    private final Method method;
    private final Param params;
    private final String path;
    private final String contentType;

    private RequestLine(final Method method, final Param params, final String path, final String contentType) {
        this.method = method;
        this.params = params;
        this.path = path;
        this.contentType = contentType;
    }

    public static RequestLine of(final String readLine) {
        final List<String> requests = Arrays.asList(Objects.requireNonNull(readLine).split(" "));
        final var method = Method.of(requests.get(0));
        final var httpParams = Param.of(getParam(requests.get(1)));
        final var path = calculatePath(requests.get(1));
        final var contentType = ContentType.getType(getExtension(path));

        return new RequestLine(method, httpParams, path, contentType);
    }

    public boolean isEmptyParam() {
        return params.isEmpty();
    }

    public Method getMethod() {
        return method;
    }

    public Param getParams() {
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getContentType() {
        return contentType;
    }
}
