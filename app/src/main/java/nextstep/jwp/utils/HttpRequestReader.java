package nextstep.jwp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.request.HttpHeaders;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;

public class HttpRequestReader {

    public static HttpRequest httpRequest(InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();

        final String[] requestLine = line.split(" ");
        final String requestMethod = requestLine[0];
        final String requestUri = requestLine[1];
        final String requestProtocol = requestLine[2];

        final HttpHeaders httpHeaders = getHttpHeaders(reader);

        if (httpHeaders.hasBody()) {
            final String requestBody = getRequestBody(
                    reader,
                    Integer.parseInt(httpHeaders.getHeaderBy("Content-Length")
                    ));
            return new HttpRequest(
                    HttpMethod.valueOf(requestMethod),
                    requestUri,
                    requestProtocol,
                    httpHeaders,
                    requestBody
            );
        }

        return new HttpRequest(
                HttpMethod.valueOf(requestMethod),
                requestUri,
                requestProtocol,
                httpHeaders
        );
    }

    private static boolean exist(String line) {
        return !"".equals(line) && !Objects.isNull(line);
    }

    private static HttpHeaders getHttpHeaders(BufferedReader reader)
            throws IOException {
        final Map<String, String> requestHeaders = new LinkedHashMap<>();

        String line;
        HttpCookie httpCookie = new HttpCookie();

        while (exist(line = reader.readLine())) {
            final String[] fields = line.split(": ");
            requestHeaders.put(fields[0], fields[1]);

            httpCookie = getHttpCookie(line, requestHeaders, httpCookie);
        }
        return new HttpHeaders(requestHeaders, httpCookie);
    }

    private static HttpCookie getHttpCookie(String line, Map<String, String> requestHeaders,
            HttpCookie httpCookie) {
        if (line.contains("Cookie")) {
            httpCookie = new HttpCookie(requestHeaders.get("Cookie"));
        }
        return httpCookie;
    }

    private static String getRequestBody(BufferedReader reader, int contentLength)
            throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
