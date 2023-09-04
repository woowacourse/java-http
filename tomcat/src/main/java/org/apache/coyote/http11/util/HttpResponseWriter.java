package org.apache.coyote.http11.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseWriter {

    private static final Logger log = LoggerFactory.getLogger(HttpResponseWriter.class);
    private static final String HEADER_DELIMITER = ": ";

    private HttpResponseWriter() {
    }

    public static void write(OutputStream outputStream, HttpResponse httpResponse)
            throws IOException {
        final var headers = httpResponse.getHeaders();
        var contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        if (contentType == null) {
            contentType = "text/html;charset=utf-8";
        }

        String response = "";
        if (httpResponse.getHttpStatus() == HttpStatus.OK) {
            response = createOkResponse(httpResponse, contentType);
        }
        if (httpResponse.getHttpStatus() == HttpStatus.FOUND) {
            response = createFoundResponse(httpResponse);
        }

        log.info(response);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private static String createOkResponse(HttpResponse httpResponse, String contentType) {
        return String.join("\r\n",
                createStatusLine(httpResponse),
                String.join(HEADER_DELIMITER, HttpHeaders.CONTENT_TYPE, contentType) + " ",
                String.join(HEADER_DELIMITER, HttpHeaders.CONTENT_LENGTH,
                        String.valueOf(httpResponse.getBody().getBytes().length)) + " ",
                "",
                httpResponse.getBody());
    }

    private static String createFoundResponse(HttpResponse httpResponse) {
        return String.join("\r\n",
                createStatusLine(httpResponse) + " ",
                setCookie(httpResponse) + " ",
                String.join(HEADER_DELIMITER, HttpHeaders.LOCATION, httpResponse.getHeader("Location")) + " ",
                "",
                httpResponse.getBody());
    }

    private static String createStatusLine(HttpResponse httpResponse) {
        return String.join(" ", httpResponse.getProtocol(),
                String.valueOf(httpResponse.getHttpStatus().getValue()),
                httpResponse.getHttpStatus().getReasonPhrase());
    }

    private static String setCookie(HttpResponse httpResponse) {
        if (httpResponse.getCookie(HttpCookie.JSESSIONID) == null) {
            return "";
        }
        String cookieValues = httpResponse.getCookies().values().stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .collect(Collectors.joining("; "));
        return String.join(HEADER_DELIMITER, HttpHeaders.SET_COOKIE, cookieValues);
    }
}
