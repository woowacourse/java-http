package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestGenerator {

    private RequestGenerator() {
    }

    public static HttpRequest generate(final BufferedReader reader) throws IOException {
        RequestLine requestLine = readRequestLine(reader);
        RequestHeader requestHeader = readHeader(reader);
        RequestBody requestBody = readRequestBody(reader, requestHeader);
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private static RequestLine readRequestLine(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return RequestLine.from(line);
    }

    private static RequestHeader readHeader(final BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
                break;
            }
            lines.add(line);
        }
        return RequestHeader.from(lines);
    }

    private static RequestBody readRequestBody(final BufferedReader reader, final RequestHeader requestHeader) throws IOException {
        if (requestHeader.getHeaderValue("Content-Type") == null) {
            return null;
        }
        int contentLength = Integer.parseInt(requestHeader.getHeaderValue("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return RequestBody.from(requestBody);
    }
}
