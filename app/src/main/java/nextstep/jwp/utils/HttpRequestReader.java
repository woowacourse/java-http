package nextstep.jwp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;

public class HttpRequestReader {

    public static HttpRequest httpRequest(InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();

        final String[] requestLine = line.split(" ");
        final String requestMethod = requestLine[0];
        final String requestUri = requestLine[1];
        final String requestProtocol = requestLine[2];

        final Map<String, String> requestHeaders = new LinkedHashMap<>();

        int contentLength = 0;
        HttpCookie httpCookie = new HttpCookie();

        while (exist(line = reader.readLine())) {
            final String[] fields = line.split(": ");
            requestHeaders.put(fields[0], fields[1]);

            contentLength = getContentLength(line, requestHeaders, contentLength);
            httpCookie = getHttpCookie(line, requestHeaders, httpCookie);
        }

        String requestBody = getRequestBody(reader, contentLength);

        return new HttpRequest(
                HttpMethod.valueOf(requestMethod),
                requestUri,
                requestProtocol,
                requestHeaders,
                requestBody,
                httpCookie
        );
    }

    private static boolean exist(String line) {
        return !"".equals(line) && !Objects.isNull(line);
    }

    private static HttpCookie getHttpCookie(String line, Map<String, String> requestHeaders,
            HttpCookie httpCookie) {
        if (line.contains("Cookie")) {
            httpCookie = new HttpCookie(requestHeaders.get("Cookie"));
        }
        return httpCookie;
    }

    private static int getContentLength(String line, Map<String, String> requestHeaders,
            int contentLength) {
        if (line.contains("Content-Type")) {
            contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
        }
        return contentLength;
    }

    private static String getRequestBody(BufferedReader reader, int contentLength)
            throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
