package nextstep.jwp.http;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.util.FileUtil;

public class HttpRequest {

    private static final String BLANK = " ";
    private static final int REQUEST_LINE_COUNT = 3;
    private static final int REQUEST_LINE_HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_LINE_URI_INDEX = 1;

    private static final String URI_QUERY_PARAM_DELIMITER = "?";

    private static final String STATIC_EXTENTION_DOT = ".";
    private static final String DEFAULT_STATIC_EXTENSION = ".html";
    private static final String ROOT_PATH = "/";

    private static final String EMPTY_QUERY_PARAMETER = "";

    private final HttpMethod httpMethod;
    private final String path;
    private final RequestParams queryParams;
    private final ContentType contentType;
    private final HttpRequestHeaders httpRequestHeaders;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final HttpMethod httpMethod, final String path, final RequestParams queryParams,
                       final ContentType contentType, final HttpRequestHeaders httpRequestHeaders,
                       final HttpRequestBody httpRequestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.contentType = contentType;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest of(final String requestLine, final HttpRequestHeaders httpRequestHeaders, final HttpRequestBody httpRequestBody) {
        String[] requestLineValues = splitRequestLine(requestLine);

        HttpMethod httpMethod = HttpMethod.from(requestLineValues[REQUEST_LINE_HTTP_METHOD_INDEX]);
        String uri = requestLineValues[REQUEST_LINE_URI_INDEX];
        String path = parsePath(uri);
        RequestParams queryParams = RequestParams.from(parseQueryParameter(uri));
        ContentType contentType = parseContentType(path);

        return new HttpRequest(httpMethod, path, queryParams, contentType, httpRequestHeaders, httpRequestBody);
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

    private static String parsePath(final String uri) {
        if (uri.contains(URI_QUERY_PARAM_DELIMITER)) {
            return uri.substring(0, uri.lastIndexOf(URI_QUERY_PARAM_DELIMITER));
        }
        return uri;
    }

    private static String parseQueryParameter(final String uri) {
        if (uri.contains(URI_QUERY_PARAM_DELIMITER)) {
            return uri.substring(uri.lastIndexOf(URI_QUERY_PARAM_DELIMITER) + 1);
        }
        return EMPTY_QUERY_PARAMETER;
    }

    private static ContentType parseContentType(final String path) {
        if (isResource(path)) {
            return ContentType.fromExtension(FileUtil.getExtension(path));
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

    public boolean isRootPath() {
        return path.equals(ROOT_PATH);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getValues();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
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
        return Objects.equals(path, that.path) && Objects.equals(queryParams, that.queryParams)
                && contentType == that.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, queryParams, contentType);
    }
}
