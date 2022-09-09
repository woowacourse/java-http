package org.apache.coyote.model.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.model.request.ContentType.HTML;

public class RequestLine {


    private static final String EXTENSION_SEPARATOR = ".";
    private static final String PARAM_START_SEPARATOR = "?";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String DEFAULT_INDEX = "/";
    private static final String PARAM_COUPLER = "=";
    private static final String PARAM_DELIMITER = "&";
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final int METHOD_INDEX = 0;
    private static final int PARAM_INDEX = 1;

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
        final var method = Method.valueOf(requests.get(METHOD_INDEX));
        final var httpParams = getParam(requests.get(PARAM_INDEX));
        final var path = calculatePath(requests.get(PARAM_INDEX));
        final var contentType = ContentType.getType(getExtension(path));

        return new RequestLine(method, httpParams, path, contentType);
    }

    public static String calculatePath(final String uri) {
        Objects.requireNonNull(uri);
        String path = uri;
        if (path.contains(PARAM_START_SEPARATOR)) {
            path = path.substring(0, uri.indexOf(PARAM_START_SEPARATOR));
        }
        if (!path.contains(EXTENSION_SEPARATOR) && !path.equals(DEFAULT_INDEX)) {
            path += DEFAULT_EXTENSION;
        }
        return path;
    }

    public static Map<String, String> getParam(final String uri) {
        Objects.requireNonNull(uri);
        if (!uri.contains(PARAM_START_SEPARATOR)) {
            return Collections.emptyMap();
        }
        return calculateParam(uri);
    }

    private static Map<String, String> calculateParam(final String uri) {
        final List<String> inputs = Arrays.asList(uri.substring(uri.indexOf(PARAM_START_SEPARATOR) + 1)
                .split(PARAM_DELIMITER));
        final Map<String, String> queryParams = new HashMap<>();
        for (String input : inputs) {
            List<String> query = Arrays.asList(input.split(PARAM_COUPLER));
            queryParams.put(query.get(KEY), query.get(VALUE));
        }
        return queryParams;
    }

    public static String getExtension(final String uri) {
        Objects.requireNonNull(uri);
        if (uri.contains(EXTENSION_SEPARATOR)) {
            return uri.substring(uri.lastIndexOf(EXTENSION_SEPARATOR) + 1);
        }
        return HTML.getExtension();
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
