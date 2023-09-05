package nextstep.org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StartLine {

    private static final String START_LINE_DELIMITER = " ";
    public static final int HTTP_METHOD_INDEX = 0;
    public static final int URL_INDEX = 1;
    public static final int HTTP_VERSION_INDEX = 2;
    public static final String QUERY_PARAM_DELIMITER = "?";

    private String httpMethod;
    private String url;
    private String httpVersion;
    private String queryString = null;

    public StartLine(String startLine) {
        List<String> parsed = Arrays.asList(startLine.split(START_LINE_DELIMITER));
        httpMethod = parsed.get(HTTP_METHOD_INDEX);
        parseUrl(parsed);
        httpVersion = parsed.get(HTTP_VERSION_INDEX);
    }

    private void parseUrl(List<String> parsed) {
        url = parsed.get(URL_INDEX);
        if (url.contains(QUERY_PARAM_DELIMITER)) {
            int queryParamIndex = url.indexOf(QUERY_PARAM_DELIMITER);
            url = parsed.get(URL_INDEX).substring(0, queryParamIndex);
            queryString = parsed.get(queryParamIndex + 1);
        }
    }

    public boolean hasQueryString() {
        return Objects.nonNull(queryString);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
