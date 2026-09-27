package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpMethod;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestLine {
    private HttpMethod httpMethod = HttpMethod.GET;
    private String path = "/";
    private String version = "HTTP/1.1";
    private final Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        String[] tokens = parse(requestLine);
        this.httpMethod = HttpMethod.of(tokens[0]);
        this.path = getPath(tokens[1]);
        this.version = tokens[2];
        params.putAll(getParams(tokens[1]));
    }


    public String[] parse(String requestLine) {
        final String[] tokens = requestLine.split(" ", 3);

        if (tokens.length != 3) {
            throw new IllegalArgumentException("잘못된 요청 줄: " + requestLine);
        }

        tokens[0] = tokens[0].trim();
        tokens[1] = tokens[1].trim();
        tokens[2] = tokens[2].trim();
        return tokens;
    }

    private String getPath(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return uri.substring(0, index);
        }
        return uri;
    }

    private Map<String, String> getParams(String uri) {
        return getQueryString(uri)
                .map(this::getParamsMap)
                .orElseGet(Collections::emptyMap);
    }

    private Optional<String> getQueryString(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return Optional.of(uri.substring(index + 1));
        }
        return Optional.empty();
    }

    private Map<String, String> getParamsMap(String str) {
        Map<String, String> paramsMap = new HashMap<>();
        String[] data = str.split("\\&");
        for (String d : data) {
            String[] param = d.split("\\=");
            String key = URLDecoder.decode(param[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(param[1], StandardCharsets.UTF_8);

            paramsMap.put(key, value);
        }
        return paramsMap;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
