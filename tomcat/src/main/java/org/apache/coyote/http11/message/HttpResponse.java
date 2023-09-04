package org.apache.coyote.http11.message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.http11.session.Cookie;

public class HttpResponse {

    private static final String DEFAULT_CHARSET = ";charset=utf-8";
    private static final String COOKIE_HEADER = "Set-Cookie";
    private static final String EMPTY_STRING = "";

    private final HttpStatus httpStatus;
    private final HttpHeaders headers;
    private final String body;

    private HttpResponse(final HttpStatus httpStatus, final HttpHeaders headers, final String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final HttpStatus httpStatus, final HttpRequest httpRequest) {
        final HttpHeaders responseHeader = createDefaultHeader(httpRequest);
        setCookie(httpRequest, responseHeader);
        return new HttpResponse(httpStatus, responseHeader, EMPTY_STRING);
    }

    public static HttpResponse ofText(final HttpStatus httpStatus, final String body, final HttpRequest httpRequest) {
        final HttpHeaders responseHeader = createDefaultHeader(httpRequest);
        responseHeader.setHeaderWithValue("Content-Length", String.valueOf(body.getBytes().length));
        setCookie(httpRequest, responseHeader);

        return new HttpResponse(httpStatus, responseHeader, body);
    }

    public static HttpResponse ofFile(final HttpStatus httpStatus, final URL url, final HttpRequest httpRequest) throws IOException {
        final HttpHeaders responseHeader = createDefaultHeader(httpRequest);
        final String file = readStaticFile(url);
        responseHeader.setHeaderWithValue("Content-Length", String.valueOf(file.getBytes().length));
        setCookie(httpRequest, responseHeader);

        return new HttpResponse(httpStatus, responseHeader, file);
    }

    private static HttpHeaders createDefaultHeader(final HttpRequest httpRequest) {
        final Map<String, String > responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type",
            ContentType.findResponseContentTypeFromRequest(httpRequest).getType() + DEFAULT_CHARSET);
        return new HttpHeaders(responseHeaders);
    }

    private static String readStaticFile(final URL url) throws IOException {
        return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
    }

    private static void setCookie(final HttpRequest httpRequest, final HttpHeaders responseHeader) {
        if (!httpRequest.hasHeader("Cookie")) {
            final Cookie cookie = new Cookie("JSESSIONID", UUID.randomUUID().toString());
            responseHeader.setHeaderWithValue(COOKIE_HEADER, cookie.getAllNamesWithValue());
        }
    }

    public void setHeader(final String field, final String value) {
        headers.setHeaderWithValue(field, value);
    }

    public String convertToMessage() {
        return String.join("\r\n",
            createStatusLine(),
            createHeaderLines(),
            "",
            body);
    }

    private String createStatusLine() {
        return String.join(" ",
            HttpProtocol.HTTP_ONE.getVersion(), String.valueOf(httpStatus.getStatusCode()), httpStatus.name() , "");
    }

    private String createHeaderLines() {
        return headers.getHeadersWithValue()
            .entrySet()
            .stream()
            .map(entry -> String.join(": ", entry.getKey(), entry.getValue()) + " ")
            .collect(Collectors.joining("\r\n"));
    }
}
