package org.apache.coyote;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.constant.HttpMethod;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;
    private static final int HTTP_METHOD = 0;
    private static final int PATH = 1;

    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod method;
    private final HttpPath path;
    private final HttpHeaders headers;

    private HttpRequest(final HttpMethod method, final HttpPath path, final HttpHeaders headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }

    public static HttpRequest from(final List<String> rawHttpRequest) {
        final List<String> request = new ArrayList<>(rawHttpRequest);

        final String startLine = request.remove(START_LINE_INDEX);
        final String[] startLineContents = startLine.split(START_LINE_DELIMITER);

        final HttpMethod httpMethod = HttpMethod.from(startLineContents[HTTP_METHOD]);
        final HttpPath path = HttpPath.from(startLineContents[PATH]);
        final HttpHeaders headers = HttpHeaders.from(request);

        return new HttpRequest(httpMethod, path, headers);
    }

    public String getPath() {
        return path.getValue();
    }

    public String getParam(final String param) {
        return path.getParam(param);
    }

    public boolean haveParam(final String param) {
        return path.getParam(param) != null;
    }
}
