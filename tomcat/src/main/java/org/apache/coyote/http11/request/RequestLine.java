package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.MethodType.GET;
import static org.apache.coyote.http11.MethodType.POST;

import java.nio.file.Path;
import org.apache.coyote.http11.MethodType;

public class RequestLine {

    private final MethodType methodType;
    private final String path;
    private final String protocolVersion;

    public static RequestLine from(String requestLine) {
        String[] requestLineParts = getRequestLineParts(requestLine);

        MethodType methodType = MethodType.getMethodType(requestLineParts[0]);
        String requestPath = requestLineParts[1];
        String protocolVersion = requestLineParts[2];

        return new RequestLine(methodType, requestPath, protocolVersion);
    }

    private RequestLine(MethodType methodType, String path, String protocolVersion) {
        this.methodType = methodType;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public Path getPath() {
        ClassLoader classLoader = getClass().getClassLoader();

        if (path.startsWith("/login")) {
            return Path.of(classLoader.getResource("static/login.html").getPath());
        }

        if (path.startsWith("/register")) {
            return Path.of(classLoader.getResource("static/register.html").getPath());
        }

        var resource = classLoader.getResource("static" + path);
        if (resource != null) {
            return Path.of(resource.getPath());
        }

        return Path.of(classLoader.getResource("static/404.html").getPath());
    }

    public boolean isDefaultPage() {
        return path.equals("/");
    }

    public boolean startsWithLogin() {
        return path.startsWith("/login");
    }

    public boolean isGetMethod() {
        return methodType == GET;
    }

    public boolean isPostMethod() {
        return methodType == POST;
    }

    private static String[] getRequestLineParts(String request) {
        String[] requestLineParts = request.split(" ");
        if (requestLineParts.length < 3) {
            throw new IllegalArgumentException("[ERROR] invalid request: " + request);
        }

        return requestLineParts;
    }
}
