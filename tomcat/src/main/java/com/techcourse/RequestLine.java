package com.techcourse;

import java.util.Arrays;
import java.util.List;

public class RequestLine {
    private final HttpProtocol method;
    private final String location;
    private final String version;
    private String QueryString;

    public RequestLine(String method, String Location, String version) {
        this.method = HttpProtocol.from(method);
        this.location = Location;
        this.version = version;
    }

    public static RequestLine from(String requestLine) {
        List<String> split = Arrays.stream(requestLine.split(" "))
                .map(String::strip)
                .toList();
        validate(split);
        return new RequestLine(split.get(0), split.get(1), split.get(2));
    }

    private static void validate(List<String> requestLine) {
        // TODO: validate
    }

    public HttpProtocol getMethod() {
        return method;
    }

    public String getLocation() {
        return location;
    }

    public String getVersion() {
        return version;
    }
}
