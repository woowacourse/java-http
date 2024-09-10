package org.apache.coyote.http;

import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpMessageGenerator {

    private HttpMessageGenerator() {
    }

    public static HttpRequest generateRequest(final BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }

        HttpRequest request = HttpRequest.of(headerLines);
        getRequestBody(reader, request);

        return request;
    }

    private static void getRequestBody(BufferedReader reader, HttpRequest request) throws IOException {
        if (request.getMethod().equals(HttpMethod.POST)) {
            int contentLength = request.getContentLength();
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            request.setBody(requestBody);
        }
    }
}
