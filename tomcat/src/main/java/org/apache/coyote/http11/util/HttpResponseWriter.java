package org.apache.coyote.http11.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpResponse;

public class HttpResponseWriter {

    private static final String HEADER_DELIMITER = ": ";

    private HttpResponseWriter() {
    }

    public static void write(OutputStream outputStream, HttpResponse httpResponse)
            throws IOException {

        StringJoiner stringJoiner = new StringJoiner(" \r\n");
        stringJoiner.add(createStatusLine(httpResponse));
        HttpHeaders headers = httpResponse.getHeaders();
        for (final Entry<String, String> entry : headers.getHeaders().entrySet()) {
            stringJoiner.add(String.join(HEADER_DELIMITER, entry.getKey(), entry.getValue()));
        }
        String cookieLine = getCookieLine(httpResponse);
        if (!cookieLine.isEmpty()) {
            stringJoiner.add(cookieLine);
        }
        stringJoiner.add("\r\n" + httpResponse.getBody());

        String response = stringJoiner.toString();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private static String createStatusLine(HttpResponse httpResponse) {
        return String.join(" ", httpResponse.getProtocol(),
                String.valueOf(httpResponse.getHttpStatus().getValue()),
                httpResponse.getHttpStatus().getReasonPhrase());
    }

    private static String getCookieLine(HttpResponse httpResponse) {
        if (httpResponse.getCookie(HttpCookie.JSESSIONID) == null) {
            return "";
        }
        String cookieValues = httpResponse.getCookies().values().stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .collect(Collectors.joining("; "));
        return String.join(HEADER_DELIMITER, HttpHeaders.SET_COOKIE, cookieValues);
    }
}
