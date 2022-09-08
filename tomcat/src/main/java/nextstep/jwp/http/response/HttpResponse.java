package nextstep.jwp.http.response;

import static nextstep.jwp.http.common.HttpHeaders.CONTENT_LENGTH;
import static nextstep.jwp.http.common.HttpHeaders.CONTENT_TYPE;
import static nextstep.jwp.http.common.HttpHeaders.LOCATION;
import static nextstep.jwp.http.common.HttpHeaders.SET_COOKIE;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import nextstep.jwp.http.common.ContentType;
import nextstep.jwp.http.common.HttpCookie;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.common.Session;
import nextstep.jwp.http.common.SessionManager;
import nextstep.jwp.http.util.FileReader;

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

    public void addCookie(final Object object) {
        HttpCookie httpCookie = SessionManager.createCookie();
        Session session = new Session(httpCookie.getKey());
        session.addAttribute("user", object);
        SessionManager.addSession(httpCookie.getValue(), session);
        httpHeaders.addSetCookie(httpCookie);
    }

    public void sendResource(final String path) throws IOException {
        File file = FileReader.getFile(path);
        byte[] responseBody = Files.readAllBytes(file.toPath());

        httpHeaders.add(CONTENT_TYPE, Files.probeContentType(file.toPath()));
        httpHeaders.add(CONTENT_LENGTH, String.valueOf(responseBody.length));

        this.responseBody = new ResponseBody(new String(responseBody));
    }

    public void sendRedirect(final HttpStatus httpStatus, final String path) throws IOException {
        this.httpStatus = httpStatus;
        File file = FileReader.getFile(path);

        httpHeaders.add(CONTENT_TYPE, Files.probeContentType(file.toPath()));
        httpHeaders.add(LOCATION, path);

        this.responseBody = new ResponseBody(new String(Files.readAllBytes(file.toPath())));
    }

    public void sendError(final HttpStatus httpStatus, final String path) throws IOException {
        this.httpStatus = httpStatus;
        File file = FileReader.getFile(path);

        httpHeaders.add(CONTENT_TYPE, Files.probeContentType(file.toPath()));
        httpHeaders.add(LOCATION, path);

        this.responseBody = new ResponseBody(new String(Files.readAllBytes(file.toPath())));
    }

    public String getHttpResponse() {
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
