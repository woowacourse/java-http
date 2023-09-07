package nextstep.org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StartLine {

    private static final String START_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String QUERY_PARAM_DELIMITER = "?";

    private String httpMethod;
    private String path;
    private String queryString = null;
    private String httpVersion;

    public StartLine(String startLine) {
        List<String> parsedStartLine = Arrays.asList(startLine.split(START_LINE_DELIMITER));
        httpMethod = parsedStartLine.get(HTTP_METHOD_INDEX);
        parsePath(parsedStartLine);
        httpVersion = parsedStartLine.get(HTTP_VERSION_INDEX);
    }

    private void parsePath(List<String> parsed) {
        path = parsed.get(REQUEST_TARGET_INDEX);
        if (path.contains(QUERY_PARAM_DELIMITER)) {
            int queryParamIndex = path.indexOf(QUERY_PARAM_DELIMITER);
            path = parsed.get(REQUEST_TARGET_INDEX).substring(0, queryParamIndex);
            queryString = parsed.get(queryParamIndex + 1);
        }
    }

    public boolean hasQueryString() {
        return Objects.nonNull(queryString);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }
}
