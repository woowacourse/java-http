package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final String protocolVersion;
    private String status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String path;

    public HttpResponse(String protocolVersion, String path) {
        this.protocolVersion = protocolVersion;
        this.path = path;
        this.status = "200 OK";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String response() throws IOException {
        String responseBody = makeResponseBody();
        addContentType();

        headers.put("Content-Length", responseBody.getBytes().length + "");

        String response = String.join("\r\n",
                protocolVersion.trim() + " " + status.trim() + " ",
                responseHeaders()
        );

        response = String.join("\r\n", response, responseBody);

        return response;
    }

    private void addContentType() {
        String type = "html";
        if (path.contains(".")) {
            type = List.of(path.split("\\.")).getLast();
        }
        headers.put("Content-Type", "text/" + type + ";charset=utf-8");
    }

    private String responseHeaders() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.append(header.getKey()).append(": ");
            builder.append(header.getValue()).append(" ");
            builder.append("\r\n");
        }
        return builder.toString();
    }

    private String makeResponseBody() throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }
        if (path.equals("/login") || path.equals("/register")) {
            path = path + ".html";
        }
        final URL resource = getClass().getClassLoader().getResource("static" + path);

        final String filePath = resource.getFile();
        final Path path = Paths.get(filePath);

        return Files.readString(path);
    }
}
