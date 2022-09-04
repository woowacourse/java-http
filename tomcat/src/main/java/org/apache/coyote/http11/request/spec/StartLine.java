package org.apache.coyote.http11.request.spec;

import java.util.List;
import java.util.Map;

public class StartLine {

    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String DELIMITER = " ";

    private final HttpMethod method;
    private final RequestUrl requestUrl;
    private final Protocol protocol;

    public StartLine(HttpMethod method, RequestUrl requestUrl, Protocol protocol) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.protocol = protocol;
    }

    public static StartLine from(String line) {
        String[] components = line.split(DELIMITER);
        validateComponentsCount(components);
        return new StartLine(
                HttpMethod.from(components[METHOD_INDEX]),
                RequestUrl.from(components[URL_INDEX]),
                new Protocol(components[PROTOCOL_INDEX]));
    }

    public boolean hasQueryParams() {
        return requestUrl.hasQueryParams();
    }

    private static void validateComponentsCount(String[] components) {
        if (components.length < List.of(METHOD_INDEX, URL_INDEX, PROTOCOL_INDEX).size()) {
            throw new IllegalArgumentException("There are missing components in start line");
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return requestUrl.getPath();
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Map<String, String> getParams() {
        return requestUrl.getParams();
    }

    public void setPath(String pathName) {
        requestUrl.setPath(pathName);
    }
}
