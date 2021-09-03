package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static nextstep.jwp.controller.JwpController.NOT_FOUND_RESPONSE;

public class HttpResponse {
    private String response;

    public HttpResponse() {
    }

    public HttpResponse(final HttpStatus status, final HttpContentType httpContentType, final String location) {
        this.response = makeContentResponse(status, httpContentType, location);
    }

    public HttpResponse(final HttpStatus status, final HttpCookie httpCookie, final String location) {
        this.response = makeContentResponseWithCookie(status, httpCookie, location);
    }

    private String makeContentResponse(final HttpStatus status, final HttpContentType httpContentType, final String contentUrl) {
        try {
            return getHttpResponse(status, httpContentType, getFileResponse("static/" + contentUrl));
        } catch (RuntimeException e) {
            return NOT_FOUND_RESPONSE.getResponse();
        }
    }

    private String makeContentResponseWithCookie(final HttpStatus status, final HttpCookie httpCookie, final String contentUrl) {
        try {
            return getHttpResponseWithCookie(status, httpCookie, getFileResponse("static/" + contentUrl));
        } catch (RuntimeException e) {
            return NOT_FOUND_RESPONSE.getResponse();
        }
    }

    private String getHttpResponse(final HttpStatus status, final HttpContentType httpContentType, final String responseBody) {
        List<String> responses = List.of("HTTP/1.1 " + status.toString(),
                "Content-Type: " + httpContentType.getContentTypeResponse(),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        return String.join("\r\n", responses);
    }

    private String getHttpResponseWithCookie(final HttpStatus status, final HttpCookie httpCookie, final String responseBody) {
        List<String> responses = List.of("HTTP/1.1 " + status.toString(),
                "Set-Cookie: " + httpCookie.toString(),
                "Content-Type: " + HttpContentType.NOTHING.getContentTypeResponse(),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        return String.join("\r\n", responses);
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
