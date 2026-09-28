package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {
    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addBody(byte[] body) {
        this.body = body.clone();
        headers.put("Content-Length", String.valueOf(body.length));
    }

    public void fromResource(String resourceName) throws IOException {
        byte[] responseBody = readResource(resourceName);

        addHeader("Content-Type", resolveContentType(resourceName));
        addBody(responseBody);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(headers));
    }

    public byte[] getBody() {
        return body.clone();
    }

    private static String resolveContentType(String resourceName) {
        if (resourceName.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }

        if (resourceName.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }

        return "application/octet-stream";
    }

    private byte[] readResource(String resourceName) throws IOException {
        final URL resource = Objects.requireNonNull(
                getClass().getClassLoader()
                        .getResource("static/" + resourceName),
            "해당 리소스를 찾을 수 없습니다.");

        final Path path = new File(resource.getFile()).toPath();

        return Files.readAllBytes(path);
    }
}
