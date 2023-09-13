package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpVersion;

public class HttpResponse {

    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String BLANK = " ";
    private static final String EMPTY_STRING = "";
    private static final String STATIC_PATH = "static";
    private static final String ROOT_PATH = "/";

    private final HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
    private HttpStatusLine httpStatusLine;
    private String path;
    private String body;
    private boolean isRedirect = false;

    public String get() throws IOException {
        final String contentType = ContentType.getByPath(path) + CHARSET_UTF_8;
        if (path.equals(ROOT_PATH)) {
            return makeResponse(contentType);
        }
        if (isRedirect) {
            if (httpResponseHeader.hasCookie()) {
                return makeRedirectResponseWithCookie();
            }
            return makeRedirectResponse();
        }

        final URL resource = HttpResponse.class.getClassLoader().getResource(STATIC_PATH + path);
        if (resource == null) {
            return EMPTY_STRING;
        }
        this.body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return makeResponse(contentType);
    }

    private String makeResponse(final String contentType) {
        if (httpResponseHeader.hasCookie()) {
            return makeGeneralResponseWithCookie(contentType, body);
        }
        return makeGeneralResponse(contentType, body);
    }

    private String makeRedirectResponse() {
        final HttpVersion httpVersion = httpStatusLine.getHttpVersion();
        final HttpStatus status = httpStatusLine.getHttpStatus();
        return String.join("\r\n",
                httpVersion.getValue() + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Location: " + path + BLANK,
                ""
        );
    }

    private String makeRedirectResponseWithCookie() {
        final HttpVersion httpVersion = httpStatusLine.getHttpVersion();
        final HttpStatus status = httpStatusLine.getHttpStatus();
        final HttpCookie cookie = httpResponseHeader.getCookie();
        return String.join("\r\n",
                httpVersion.getValue() + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Set-Cookie: " + cookie.printValues() + BLANK,
                "Location: " + path + BLANK,
                ""
        );
    }

    private String makeGeneralResponseWithCookie(final String contentType, final String body) {
        final HttpVersion httpVersion = httpStatusLine.getHttpVersion();
        final HttpStatus status = httpStatusLine.getHttpStatus();
        final HttpCookie cookie = httpResponseHeader.getCookie();
        return String.join("\r\n",
                httpVersion.getValue() + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Set-Cookie: " + cookie.printValues() + BLANK,
                "Content-Type: " + contentType + BLANK,
                "Content-Length: " + body.getBytes().length + BLANK,
                "",
                body
        );
    }

    private String makeGeneralResponse(
            final String contentType,
            final String body
    ) {
        final HttpVersion httpVersion = httpStatusLine.getHttpVersion();
        final HttpStatus status = httpStatusLine.getHttpStatus();
        return String.join("\r\n",
                httpVersion.getValue() + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Content-Type: " + contentType + BLANK,
                "Content-Length: " + body.getBytes().length + BLANK,
                "",
                body
        );
    }

    public void addCookie(final HttpCookie cookie) {
        this.httpResponseHeader.setCookie(cookie);
    }

    public void sendRedirect(final String path) {
        this.path = path;
        this.isRedirect = true;
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatusLine = HttpStatusLine.from(httpStatus);
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public void setBody(final String body) {
        this.body = body;
    }
}
