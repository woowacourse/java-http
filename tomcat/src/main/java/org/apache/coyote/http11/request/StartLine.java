package org.apache.coyote.http11.request;

import java.util.List;
import org.apache.coyote.http11.HttpMethod;

public class StartLine {

    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String DELIMITER = " ";

    private final HttpMethod method;
    private final URL url;
    private final Protocol protocol;

    public StartLine(HttpMethod method, URL url, Protocol protocol) {
        this.method = method;
        this.url = url;
        this.protocol = protocol;
    }

    public static StartLine from(String line) {
        String[] components = line.split(DELIMITER);
        validateComponentsCount(components);
        return new StartLine(
                HttpMethod.of(components[METHOD_INDEX]),
                new URL(components[URL_INDEX]),
                new Protocol(components[PROTOCOL_INDEX]));
    }

    private static void validateComponentsCount(String[] components) {
        if (components.length < List.of(METHOD_INDEX, URL_INDEX, PROTOCOL_INDEX).size()) {
            throw new IllegalArgumentException("There are missing components in start line");
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URL getUrl() {
        return url;
    }

    public Protocol getProtocol() {
        return protocol;
    }
}
