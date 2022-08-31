package nextstep.jwp.model;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.util.FileNameUtil;

public class HttpRequest {

    private static final String BLANK = " ";
    private static final int REQUEST_LINE_COUNT = 3;
    private static final int REQUEST_LINE_HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_LINE_URI_INDEX = 1;

    private static final String URI_QUERY_PARAM_DELIMITER = "?";

    private static final String STATIC_EXTENTION_DOT = ".";
    private static final String DEFAULT_STATIC_EXTENSION = ".html";

    private static final String EMPTY_QUERY_PARAMETER = "";

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";

    private final String uri;
    private final String path;
    private final Map<String, String> queryParams;
    private final ContentType contentType;
    private final HttpMethod httpMethod;

    public HttpRequest(final String uri, final String path, final Map<String, String> queryParams,
                       final ContentType contentType, final HttpMethod httpMethod) {
        this.uri = uri;
        this.path = path;
        this.queryParams = queryParams;
        this.contentType = contentType;
        this.httpMethod = httpMethod;
    }

    public static HttpRequest from(final String requestLine) {
        String[] requestLineValues = splitRequestLine(requestLine);

        String uri = requestLineValues[REQUEST_LINE_URI_INDEX];
        String path = getPath(uri);
        Map<String, String> queryParams = getQueryParam(getQueryParameter(uri));
        ContentType contentType = getContentType(path);
        HttpMethod httpMethod = HttpMethod.from(requestLineValues[REQUEST_LINE_HTTP_METHOD_INDEX]);

        return new HttpRequest(uri, path, queryParams, contentType, httpMethod);
    }

    private static String[] splitRequestLine(final String requestLine) {
        checkNullRequest(requestLine);
        String[] requestLineValues = requestLine.split(BLANK);
        checkRequestLineFormat(requestLineValues);
        return requestLineValues;
    }

    private static void checkNullRequest(final String line) {
        if (line == null) {
            throw new UncheckedServletException("request line은 null이 들어올 수 없습니다.");
        }
    }

    private static void checkRequestLineFormat(final String[] requests) {
        if (requests.length != REQUEST_LINE_COUNT) {
            throw new UncheckedServletException("요청의 포맷이 잘못되었습니다.");
        }
    }

    private static String getPath(final String uri) {
        if (uri.contains(URI_QUERY_PARAM_DELIMITER)) {
            return uri.substring(0, uri.lastIndexOf(URI_QUERY_PARAM_DELIMITER));
        }
        return uri;
    }

    private static String getQueryParameter(final String uri) {
        if (uri.contains(URI_QUERY_PARAM_DELIMITER)) {
            return uri.substring(uri.lastIndexOf(URI_QUERY_PARAM_DELIMITER) + 1);
        }
        return EMPTY_QUERY_PARAMETER;
    }

    private static Map<String, String> getQueryParam(final String queryParameter) {
        Map<String, String> queryParams = new ConcurrentHashMap<>();
        if (queryParameter.equals(EMPTY_QUERY_PARAMETER)) {
            return queryParams;
        }
        String[] queryParamUris = queryParameter.split(QUERY_PARAM_DELIMITER);
        for (String queryParamUri : queryParamUris) {
            String[] param = queryParamUri.split(QUERY_PARAM_VALUE_DELIMITER);
            queryParams.put(param[0], param[1]);
        }
        return queryParams;
    }

    private static ContentType getContentType(final String path) {
        if (isResource(path)) {
            return ContentType.fromExtension(FileNameUtil.getExtension(path));
        }
        return ContentType.TEXT_HTML;
    }

    private static boolean isResource(final String path) {
        return path.contains(STATIC_EXTENTION_DOT);
    }

    public String getFilePath() {
        if (path.contains(STATIC_EXTENTION_DOT)) {
            return path;
        }
        return path + DEFAULT_STATIC_EXTENSION;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpRequest that = (HttpRequest) o;
        return Objects.equals(uri, that.uri) && Objects.equals(path, that.path) && Objects
                .equals(queryParams, that.queryParams) && contentType == that.contentType
                && httpMethod == that.httpMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, path, queryParams, contentType, httpMethod);
    }
}
