package nextstep.jwp.http;

import java.util.List;
import nextstep.jwp.utils.FileUtils;

public class HttpRequest {

    private static final String REQUEST_LINE_SEPARATOR = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final String URI_PATH_SEPARATOR = "\\?";
    private static final int PATH_INDEX = 0;
    private static final int QUERY_PARAM_INDEX = 1;
    private static final int ONLY_PATH_SIZE = 1;

    private final HttpMethod httpMethod;
    private final String uri;
    private final String path;
    private final QueryParams queryParams;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public HttpRequest(HttpMethod httpMethod, String uri, String path, QueryParams queryParams,
                       HttpHeaders httpHeaders, String requestBody) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.path = path;
        this.queryParams = queryParams;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(String requestLine, HttpHeaders httpHeaders,
                                 String requestBody) {
        List<String> request = List.of(requestLine.split(REQUEST_LINE_SEPARATOR));
        HttpMethod httpMethod = HttpMethod.from(request.get(HTTP_METHOD_INDEX));
        String uri = request.get(URI_INDEX);
        List<String> separatedUri = List.of(uri.split(URI_PATH_SEPARATOR));
        return handleHavingQueryParams(httpMethod, uri, separatedUri, httpHeaders, requestBody);
    }

    private static HttpRequest handleHavingQueryParams(HttpMethod httpMethod, String uri,
                                                       List<String> separatedUri,
                                                       HttpHeaders httpHeaders,
                                                       String requestBody) {
        if (hasQueryParam(separatedUri)) {
            return new HttpRequest(httpMethod, uri, separatedUri.get(PATH_INDEX),
                QueryParams.from(separatedUri.get(QUERY_PARAM_INDEX)), httpHeaders, requestBody);
        }
        return new HttpRequest(httpMethod, uri, uri, QueryParams.empty(), httpHeaders, requestBody);
    }

    private static boolean hasQueryParam(List<String> separatedUri) {
        return separatedUri.size() != ONLY_PATH_SIZE;
    }

    public boolean matches(String path, HttpMethod httpMethod) {
        return this.path.equals(path) && this.httpMethod.equals(httpMethod);
    }

    public boolean matches(ContentType contentType) {
        return FileUtils.extractFileExtension(path) == contentType;
    }

    public ContentType getFileExtension() {
        return FileUtils.extractFileExtension(path);
    }

    public QueryParams getFormData() {
        if (httpHeaders.matchesContentType("application/x-www-form-urlencoded")) {
            return QueryParams.from(requestBody);
        }
        return QueryParams.empty();
    }

    public String getPath() {
        return path;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
