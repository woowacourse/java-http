package nextstep.jwp.http.response;

import static nextstep.jwp.http.common.HttpHeaders.CONTENT_LENGTH;
import static nextstep.jwp.http.common.HttpHeaders.CONTENT_TYPE;
import static nextstep.jwp.http.common.HttpHeaders.LOCATION;
import static nextstep.jwp.http.common.HttpHeaders.SET_COOKIE;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import nextstep.jwp.http.common.ContentType;
import nextstep.jwp.http.common.HttpCookie;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;

public class HttpResponse {

    private static final String CHARSET_UTF_8 = ";charset=utf-8 ";
    private static final String BLANK = " ";
    private static final String EMPTY = "";

    private HttpStatus httpStatus;
    private HttpHeaders httpHeaders;
    private ResponseBody responseBody;

    public HttpResponse() {
        this.httpStatus = HttpStatus.OK;
        this.httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        this.responseBody = new ResponseBody(EMPTY);
    }

    public void addHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addResponseBody(final String text) {
        byte[] responseBody = text.getBytes(StandardCharsets.UTF_8);

        httpHeaders.add(CONTENT_TYPE, ContentType.TEXT_HTML.getType());
        httpHeaders.add(CONTENT_LENGTH, String.valueOf(responseBody.length));

        this.responseBody = new ResponseBody(text);
    }

    public void addResponseBody(final File file) throws IOException {
        Path path = file.toPath();
        byte[] responseBody = Files.readAllBytes(file.toPath());

        httpHeaders.add(CONTENT_TYPE, Files.probeContentType(path));
        httpHeaders.add(CONTENT_LENGTH, String.valueOf(responseBody.length));

        this.responseBody = new ResponseBody(new String(responseBody));
    }

    public void addRedirect(final File file, final String location) throws IOException {
        Path path = file.toPath();
        byte[] responseBody = Files.readAllBytes(file.toPath());

        httpHeaders.add(CONTENT_TYPE, Files.probeContentType(path));
        httpHeaders.add(LOCATION, location);

        this.responseBody = new ResponseBody(new String(responseBody));
    }

    public void addCookie(final HttpCookie httpCookie) {
        httpHeaders.addSetCookie(httpCookie);
    }

    @Override
    public String toString() {
        if (httpHeaders.isExistSetCookie()) {
            String response = String.join("\r\n",
                "HTTP/1.1" + BLANK + httpStatus.getCode() + BLANK + httpStatus.getDescription() + BLANK,
                "Set-Cookie: " + httpHeaders.getHeader(SET_COOKIE),
                "Content-Type: " + httpHeaders.getHeader(CONTENT_TYPE) + CHARSET_UTF_8,
                "Content-Length: " + responseBody.getValue().getBytes().length + BLANK,
                EMPTY,
                responseBody.getValue());

            return response;
        }
        String response = String.join("\r\n",
            "HTTP/1.1" + BLANK + httpStatus.getCode() + BLANK + httpStatus.getDescription() + BLANK,
            "Content-Type: " + httpHeaders.getHeader(CONTENT_TYPE) + CHARSET_UTF_8,
            "Content-Length: " + responseBody.getValue().getBytes().length + BLANK,
            EMPTY,
            responseBody.getValue());

        return response;
    }
}
