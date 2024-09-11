package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.header.HttpHeader;

public class HttpResponse {
    public static final String STATIC_PATH = "static";
    public static final String LINE_BREAK = "\n";
    private static final String DELIMITER = "\r\n";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK ";
    private static final String FOUND_STATUS_LINE = "HTTP/1.1 302 Found ";
    private static final String EMPTY_LINE = "";

    private String statusLine;
    private final List<String> headers = new ArrayList<>();
    private String body = "";

    public void ok() {
        statusLine = OK_STATUS_LINE;
    }

    public void found() {
        statusLine = FOUND_STATUS_LINE;
    }

    public byte[] getBytes() {
        final String headerString = String.join("\r\n", headers);
        return String.join(DELIMITER, statusLine, headerString, EMPTY_LINE, body).getBytes();
    }

    public void setContentType(final FileType fileType) {
        headers.add(HttpHeader.contentType(fileType));
    }

    public void setContentOfPlainText(final String plainText) {
        body = plainText;
        headers.add(HttpHeader.contentLengthOf(plainText));
    }

    public void setContentOfResources(final String filePath) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(filePath);
        body = responseBody;
        headers.add(HttpHeader.contentLengthOf(responseBody));
    }

    private String buildResponseBodyFromStaticFile(final String filePath) throws IOException {
        final var resourceName = STATIC_PATH + filePath;
        final var resourceURL = this.getClass().getClassLoader().getResource(resourceName);
        if (resourceURL == null) {
            throw new IllegalArgumentException("존재하지 않는 정적 리소스입니다.");
        }
        final var path = Path.of(resourceURL.getPath());

        return String.join(LINE_BREAK, Files.readAllLines(path)) + LINE_BREAK;
    }

    public void addCookies(final HttpCookie cookie) {
        headers.addFirst(HttpHeader.setCookie(cookie));
    }

    public void sendRedirect(final String location) {
        headers.add(HttpHeader.location(location));
    }
}
