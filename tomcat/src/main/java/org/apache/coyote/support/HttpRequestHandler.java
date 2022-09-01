package org.apache.coyote.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class HttpRequestHandler {

    private final Method method;
    private final String uri;

    private HttpRequestHandler(Method method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public static HttpRequestHandler of(List<String> request) {
        final var startLine = request.get(0).split(" ");
        final var method = Method.valueOf(startLine[0]);
        var uri = startLine[1];
        if (Method.GET.equals(method) && uri.contains("?")) {
            final var uriAndParams = uri.split("\\?");
            uri = uriAndParams[0];
        }
        return new HttpRequestHandler(method, uri);
    }

    public String handle() throws IOException {
        if (method.equals(Method.GET)) {
            return get();
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    public String get() throws IOException {
        String responseBody = findResource();
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String findResource() throws IOException {
        File file = new File(findUrl().getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private URL findUrl() {
        final var classLoader = getClass().getClassLoader();
        if (uri.equals("/")) {
            return classLoader.getResource("static/index.html");
        }
        return classLoader.getResource("static" + uri);
    }
}
