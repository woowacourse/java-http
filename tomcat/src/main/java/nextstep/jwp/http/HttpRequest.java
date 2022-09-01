package nextstep.jwp.http;

import static nextstep.jwp.http.ContentType.TEXT_HTML;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpRequest {

    private static final String BLANK = " ";
    private static final String URI_QUERY_PARAM_DELIMITER = "?";
    private static final String EXTENSION_DELIMITER = ".";

    private static final int REQUEST_LINE_COUNT = 3;
    private static final int METHOD_POSITION = 0;
    private static final int URI_POSITION = 1;
    private static final String QUERY_PARAMS_DELIMITER = "&";
    private static final String QUERY_PARAM_DELIMITER = "=";
    private static final String BLANK_QUERY_PARAMS = "";

    private final String method;
    private final String uri;
    private final String path;
    private final String contentType;
    private final Map<String, String> queryParams;

    public HttpRequest(final String method,
                       final String uri,
                       final String path,
                       final String contentType,
                       final Map<String, String> queryParams) {
        this.method = method;
        this.uri = uri;
        this.path = path;
        this.contentType = contentType;
        this.queryParams = queryParams;
    }

    public static HttpRequest from(final String requestLine) {
        String[] requestValues = splitRequestLine(requestLine);

        String method = getMethod(requestValues, METHOD_POSITION);
        String uri = getUri(requestValues, URI_POSITION);
        String path = getPath(uri);
        String contentType = getContentType(path);
        Map<String, String> queryParams = getQueryParams(uri);

        return new HttpRequest(method, uri, path, contentType, queryParams);
    }

    private static String[] splitRequestLine(final String requestLine) {
        String[] requestValues = requestLine.split(BLANK);
        checkRequestLineFormat(requestValues);
        return requestValues;
    }

    private static void checkRequestLineFormat(final String[] requests) {
        if (requests.length != REQUEST_LINE_COUNT) {
            throw new UncheckedServletException("요청의 포맷이 잘못되었습니다.");
        }
    }

    private static String getMethod(final String[] requestValues, final int methodPosition) {
        return requestValues[methodPosition];
    }

    private static String getUri(final String[] requestValues, final int uriPosition) {
        return requestValues[uriPosition];
    }

    private static String getPath(final String uri) {
        if (uri.contains(URI_QUERY_PARAM_DELIMITER)) {
            return uri.substring(0, uri.lastIndexOf(URI_QUERY_PARAM_DELIMITER));
        }
        return uri;
    }

    private static String getContentType(final String uri) {
        if (validateUriDot(uri)) {
            return ContentType.from(splitUriExtension(uri)).getType();
        }
        return TEXT_HTML.getType();
    }

    private static boolean validateUriDot(final String uri) {
        return uri.contains(EXTENSION_DELIMITER);
    }

    private static String splitUriExtension(final String path) {
        return path.substring(path.lastIndexOf(EXTENSION_DELIMITER) + 1);
    }

    private static Map<String, String> getQueryParams(final String uri) {
        Map<String, String> queryParams = new HashMap<>();
        String queryParameters = getQueryParameter(uri);
        if (queryParameters.equals(BLANK_QUERY_PARAMS)) {
            return queryParams;
        }
        String[] queryParameterValues = splitQueryParams(queryParameters);
        for (String queryParameterValue : queryParameterValues) {
            String[] param = queryParameterValue.split(QUERY_PARAM_DELIMITER);
            queryParams.put(param[0], param[1]);
        }

        return queryParams;
    }

    private static String getQueryParameter(final String uri) {
        if (uri.contains(URI_QUERY_PARAM_DELIMITER)) {
            return uri.substring(uri.lastIndexOf(URI_QUERY_PARAM_DELIMITER) + 1);
        }
        return BLANK_QUERY_PARAMS;
    }

    private static String[] splitQueryParams(final String queryParameter) {
        return queryParameter.split(QUERY_PARAMS_DELIMITER);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
