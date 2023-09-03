package org.apache.coyote.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestLine {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLine.class);
    private static final int REQUEST_LINE_TOKENS_SIZE = 3;

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> parameters;
    private final String httpVersion;

    private HttpRequestLine(HttpMethod httpMethod, String path, Map<String, String> parameters, String httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.parameters = parameters;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine decode(String requestLineString) {
        String[] tokens = requestLineString.split(" ");
        validateRequestLine(tokens);

        HttpMethod httpMethod = HttpMethod.from(tokens[0]);
        String path = decodePath(tokens[1]);
        Map<String, String> parameters = decodeParameters(tokens[1]);

        return new HttpRequestLine(httpMethod, path, parameters, tokens[2]);
    }

    private static void validateRequestLine(String[] tokens) {
        if (tokens.length != REQUEST_LINE_TOKENS_SIZE || !tokens[2].startsWith("HTTP")) {
            log.error("request line: {}", String.join(" ", tokens));
        }
    }

    private static String decodePath(String requestUri) {
        return requestUri.split("\\?")[0];
    }

    private static Map<String, String> decodeParameters(String requestUri) {
        String[] split = requestUri.split("\\?");
        if (split.length < 2) {
            return Collections.emptyMap();
        }

        String parametersString = split[1];
        return HttpParameterDecoder.decode(parametersString);
    }

    public HttpMethod getMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }
}
