package org.apache.catalina.vo;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Collections;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final Path path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String path, String version, Map<String, String> headers, String body) {
        validateVersion(version);
        this.method = method;
        this.path = new Path(path);
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    private void validateVersion(String version) {
        if (!version.equalsIgnoreCase("HTTP/1.1")) {
            throw new IllegalArgumentException();
        }
    }

    public String extractMimeType() {
//        if (headers.containsKey("Accept")) {
//            return headers.get("Accept");
//        }
        // TODO:
        // 파일 요청이 아닌 경우 모두 JSON 응답
        final var fileExtension = path.getFileExtension()
                .orElseGet(() -> FileExtension.JSON);
        return Mime.getMimeTypeValue(fileExtension);
    }

    public Session getSession(final boolean create) {
        final var cookies = getCookies();
        final var session = cookies.extractSession();
        if (session == null) {
            if (create) {
                final var sessionManager = SessionManager.getInstance();
                return sessionManager.generateNewSession();
            }
            return null;
        }
        return session;
    }

    public String getURI() {
        return path.getURI();
    }

    public Map<String, String> getPathParams() {
        return path.getQueryParams();
    }

    private Cookies getCookies() {
        if (headers.containsKey("Cookie")) {
            return new Cookies(headers.get("Cookie"));
        }
        return new Cookies();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path.getValue();
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public String getBody() {
        return body;
    }
}
