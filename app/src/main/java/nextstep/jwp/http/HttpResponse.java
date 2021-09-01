package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponse {
    private String response;

    public HttpResponse() {
    }

    public HttpResponse(final HttpStatus status, final String location) {
        this.response = makeRedirectResponse(status, location);
    }

    public HttpResponse(final HttpStatus status, final HttpContentType httpContentType, final String location) {
        this.response = makeContentResponse(status, httpContentType, location);
    }

    private static String getHttpResponse(final HttpStatus status, final HttpContentType httpContentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.toString(),
                "Content-Type: " + httpContentType.getContentTypeResponse(),
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);
    }

    private static String getRedirectHttpResponse(final HttpStatus status, final String location) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.toString(),
                "Location: " + location,
                "");
    }

    private String makeRedirectResponse(final HttpStatus status, final String location) {
        try {
            return getRedirectHttpResponse(status, location);
        } catch (RuntimeException e) {
            return getHttpResponse(HttpStatus.NOT_FOUND, HttpContentType.NOTHING, "404.html");
        }
    }

    private String makeContentResponse(final HttpStatus status, final HttpContentType httpContentType, final String responseBody) {
        try {
            return getHttpResponse(status, httpContentType, getFileResponse("static/" + responseBody));
        } catch (RuntimeException e) {
            return getHttpResponse(HttpStatus.NOT_FOUND, HttpContentType.NOTHING, "404.html");
        }
    }

    public String getFileResponse(final String request) {
        try {
            final Path path = new File(getPath(request)).toPath();
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("옳지 않은 url입니다.");
        }
    }

    private String getPath(final String request) {
        final URL url = getClass().getClassLoader().getResource(request);
        if (url == null) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
        return url.getPath();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(final String response) {
        this.response = response;
    }
}
