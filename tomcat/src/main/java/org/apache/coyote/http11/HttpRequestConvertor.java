package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
            if (method.isMethod("GET") && !path.contains(".")) {
                path += ".html";
            }
            String version = headerFirstLine[2];
            Map<String, String> headers = getHeaders(bufferedReader);

            HttpRequestHeader httpRequestHeader = new HttpRequestHeader(method, path, version, headers);

            if (httpRequestHeader.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(httpRequestHeader.getValue("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String body = new String(buffer);
                HttpRequestBody httpRequestBody = new HttpRequestBody(body);

                return new HttpRequest(httpRequestHeader, httpRequestBody);
            }

            return new HttpRequest(httpRequestHeader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] requestLine = line.split(": ");
            headers.put(requestLine[0], requestLine[1]);
        }

        return headers;
    }
}
