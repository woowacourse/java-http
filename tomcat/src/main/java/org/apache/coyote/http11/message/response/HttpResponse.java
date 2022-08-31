package org.apache.coyote.http11.message.response;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private final String startLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(String startLine, Map<String, String> headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse from(BufferedReader reader) {
        String path = parsePath(reader);

        String responseBody = createResponseBody(path);
        Map<String, String> headers = createHeaders(path, responseBody);
        String startLine = createStartLine();
        return new HttpResponse(startLine, headers, responseBody);
    }

    private static String createStartLine() {
        return "HTTP/1.1 200 OK ";
    }

    private static Map<String, String> createHeaders(String path, String responseBody) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", findContentType(path) + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        return headers;
    }

    private static String findContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        return "text/html";
    }

    private static String createResponseBody(String path) {
        if (isRootRequest(path)) {
            return "Hello world!";
        }
        return ConvertToString(findStaticFile(path));
    }

    private static String parsePath(BufferedReader reader) {
        try {
            String startLine = reader.readLine();
            return startLine.split(" ")[1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String ConvertToString(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File findStaticFile(String path) {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("static/" + path);
        return new File(resource.getFile());
    }

    private static boolean isRootRequest(String path) {
        return path.equals("/");
    }

    public byte[] getAsBytes() {
        return String.join("\r\n",
                startLine,
                headers.entrySet().stream()
                        .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining("\r\n")),
                "",
                body
        ).getBytes();
    }
}
