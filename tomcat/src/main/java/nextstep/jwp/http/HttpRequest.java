package nextstep.jwp.http;

import static java.util.List.of;

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

    private final String httpMethod;
    private final String uri;
    private final String path;
    private final QueryParams queryParams;

    public HttpRequest(String httpMethod, String uri, String path, QueryParams queryParams) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpRequest from(String requestLine) {
        List<String> request = of(requestLine.split(REQUEST_LINE_SEPARATOR));
        String httpMethod = request.get(HTTP_METHOD_INDEX);
        String uri = request.get(URI_INDEX);
        List<String> separatedUri = of(uri.split(URI_PATH_SEPARATOR));
        return handleHavingQueryParams(httpMethod, uri, separatedUri);
    }

    private static HttpRequest handleHavingQueryParams(String httpMethod, String uri,
                                                       List<String> separatedUri) {
        if (hasQueryParam(separatedUri)) {
            return new HttpRequest(httpMethod, uri, separatedUri.get(PATH_INDEX),
                QueryParams.from(separatedUri.get(QUERY_PARAM_INDEX)));
        }
        return new HttpRequest(httpMethod, uri, uri, QueryParams.empty());
    }

    private static boolean hasQueryParam(List<String> separatedUri) {
        return separatedUri.size() != ONLY_PATH_SIZE;
    }

    public String getFileExtension() {
        return FileUtils.extractFileExtension(path);
    }

    public String getPath() {
        return path;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }
}
