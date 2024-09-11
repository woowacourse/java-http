package org.apache;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.request.Http11RequestHeaders;
import org.apache.coyote.http11.request.Http11RequestLine;

public class ObjectMapper {

    private ObjectMapper() {
    }

    public static byte[] serialize(HttpResponse response) {
        return response.getResponseMessage().getBytes();
    }

    public static HttpRequest deserialize(BufferedReader bufferedReader) throws IOException {
        return readRequest(bufferedReader);
    }

    private static HttpRequest readRequest(BufferedReader bufferedReader) throws IOException {
        Http11RequestLine requestLine = getLine(bufferedReader);
        Http11RequestHeaders requestHeaders = getHeaders(bufferedReader);
        Http11RequestBody requestBody = getBody(bufferedReader, requestHeaders.getContentLength());
        return new Http11Request(requestLine, requestHeaders, requestBody);
    }

    private static Http11RequestLine getLine(BufferedReader bufferedReader) throws IOException {
        return new Http11RequestLine(bufferedReader.readLine());
    }

    private static Http11RequestHeaders getHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new LinkedList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        return new Http11RequestHeaders(lines);
    }

    private static Http11RequestBody getBody(BufferedReader bufferedReader, String contentLength) throws IOException {
        try {
            int bodyLength = Integer.parseInt(contentLength);
            char[] buffer = new char[bodyLength];
            bufferedReader.read(buffer, 0, bodyLength);
            return new Http11RequestBody(new String(buffer));
        } catch (NumberFormatException e) {
            return Http11RequestBody.ofEmpty();
        }
    }
}
