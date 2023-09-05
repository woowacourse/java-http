package org.apache.coyote.http11;

import java.io.InputStream;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequestParser {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static HttpRequest parseFromSocket(InputStream inputStream) {
        try {
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());
            final String httpRequestMethod = stringTokenizer.nextToken();
            final String httpRequestUri = stringTokenizer.nextToken();

            final Map<String, String> headers = getHeaders(bufferedReader);
            final Cookies cookies = getCookies(headers);
            final String requestBody = getRequestBody(bufferedReader);

            return new HttpRequest(httpRequestUri, httpRequestMethod, requestBody, cookies);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static Cookies getCookies(Map<String, String> headers) throws IOException {
        final Cookies cookies = new Cookies();
        final String cookiesHeader = headers.get("Cookie");

        if (Objects.isNull(cookiesHeader)) {
            return cookies;
        }

        final StringTokenizer stringTokenizer = new StringTokenizer(cookiesHeader, "; ");
        while (stringTokenizer.hasMoreTokens()) {
            final String cookie = stringTokenizer.nextToken();
            addCookie(cookies, cookie);
        }

        return cookies;
    }

    private static void addCookie(final Cookies cookies, final String cookieEntry) {
        final StringTokenizer stringTokenizer = new StringTokenizer(cookieEntry, "=");
        cookies.add(
                stringTokenizer.nextToken(),
                stringTokenizer.nextToken()
        );
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> requestHeaders = new HashMap<>();
        String header;
        while ((header = bufferedReader.readLine()).length() != 0) {
            final StringTokenizer stringTokenizer = new StringTokenizer(header, ": ");
            requestHeaders.put(
                    stringTokenizer.nextToken(),
                    stringTokenizer.nextToken()
            );
        }
        return requestHeaders;
    }

    private static String getRequestBody(BufferedReader bufferedReader) throws IOException {
        final StringBuilder requestBody = new StringBuilder();
        while (bufferedReader.ready()) {
            requestBody.append((char) bufferedReader.read());
        }
        return requestBody.toString();
    }
}
