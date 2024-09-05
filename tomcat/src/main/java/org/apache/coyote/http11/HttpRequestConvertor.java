package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.HttpRequestBody;
import org.apache.coyote.http11.httprequest.HttpRequestHeader;
import org.apache.coyote.http11.httprequest.HttpRequestLine;

public class HttpRequestConvertor {

    public static HttpRequest convertHttpRequest(BufferedReader bufferedReader) {
        try {
            String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                throw new RuntimeException("요청이 비어 있습니다.");
            }

            String[] headerFirstLine = firstLine.split(" ");
            HttpMethod method = HttpMethod.getHttpMethod(headerFirstLine[0]);
            String path = headerFirstLine[1];
            String version = headerFirstLine[2];

            HttpRequestLine httpRequestLine = new HttpRequestLine(method, path, version);

            Map<String, String> headers = getHeaders(bufferedReader);

            HttpRequestHeader httpRequestHeader = new HttpRequestHeader(headers);

            if (httpRequestHeader.containsKey("Content-Length")) {
                HttpRequestBody httpRequestBody = getHttpRequestBody(bufferedReader, httpRequestHeader);

                return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
            }

            return new HttpRequest(httpRequestLine, httpRequestHeader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpRequestBody getHttpRequestBody(
            BufferedReader bufferedReader,
            HttpRequestHeader httpRequestHeader
    ) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeader.getValue("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String body = new String(buffer);
        return new HttpRequestBody(body);
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] requestLine = line.split(":");
            headers.put(requestLine[0], parseHeaderValue(requestLine));
        }

        return headers;
    }

    private static String parseHeaderValue(String[] requestLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < requestLine.length; i++) {
            sb.append(requestLine[i].strip());
        }

        return sb.toString();
    }
}
