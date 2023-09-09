package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.exception.InvalidRequestLineException;
import org.apache.coyote.http11.util.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class RequestLine {
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String NON_RESERVED_QUERY_PARAM_DELIMITER = "\\?";
    private static final String NON_RESERVED_EXTENSION_DELIMITER = "\\.";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String SPACE_DELIMITER = " ";

    private final HttpMethod method;
    private final String url;
    private final String version;
    private final Map<String, String> requestParams;

    private RequestLine(final HttpMethod method, final String url, final String version, final Map<String, String> requestParams) {
        this.method = method;
        this.url = url;
        this.version = version;
        this.requestParams = requestParams;
    }

    public static RequestLine from(final BufferedReader br) throws IOException {
        final String line = br.readLine();
        validate(line);

        final String[] splitLine = line.split(SPACE_DELIMITER);

        return new RequestLine(getMethod(splitLine[0]), splitLine[1],
                splitLine[2], parseQueryParam(splitLine[1]));
    }

    private static void validate(final String line) {
        if (line.isBlank()) {
            throw new InvalidRequestLineException();
        }
    }

    private static Map<String, String> parseQueryParam(final String url) {
        if (!url.contains(QUERY_STRING_DELIMITER)) {
            return Collections.emptyMap();
        }
        return Parser.queryParamParse(url.split(NON_RESERVED_QUERY_PARAM_DELIMITER)[1]);
    }

    public String getAbsolutePath() {
        String absolutePath = url;
        if (absolutePath.equals("/") || absolutePath.contains(EXTENSION_DELIMITER)) {
            return absolutePath;
        }
        if (absolutePath.contains(QUERY_STRING_DELIMITER)) {
            absolutePath = absolutePath.split(NON_RESERVED_QUERY_PARAM_DELIMITER)[0];
        }
        return absolutePath + EXTENSION_DELIMITER + HttpContentType.HTML.getExtension();
    }

    public String getExtension() {
        final String[] splitUrl = url.split(NON_RESERVED_EXTENSION_DELIMITER);
        if (splitUrl.length > 1) {
            return splitUrl[splitUrl.length - 1];
        }
        return EXTENSION_DELIMITER + HttpContentType.HTML.getExtension();
    }

    private static HttpMethod getMethod(final String method) {
        return HttpMethod.valueOf(method);
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }
}
