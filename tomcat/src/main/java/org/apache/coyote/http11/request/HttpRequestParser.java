package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestParser {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);

    public HttpRequest parseRequest(BufferedReader reader) throws IOException {
        RequestLine requestLine = extractRequestLine(reader);
        RequestHeader requestHeader = extractHeaders(reader);
        RequestBody requestBody = extractRequestBody(reader, requestHeader);

        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private RequestLine extractRequestLine(BufferedReader reader) throws IOException {
        String method = "";
        String path = "";
        String queryString = "";
        String protocol = "";

        String firstLine = reader.readLine();

        if (firstLine != null) {
            log.info("[Request] requestLine: {}", firstLine);

            String[] requestLineParts = firstLine.split(" ");
            method = requestLineParts[0];
            String uri = requestLineParts[1];
            protocol = requestLineParts[2];

            int index = uri.indexOf("?");
            if (index != -1) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            } else {
                path = uri;
                queryString = null;
            }
        }

        return new RequestLine(method, path, queryString, protocol);
    }

    private RequestHeader extractHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String readLine = reader.readLine();
            if (readLine == null || readLine.isEmpty()) {
                break;
            }

            String[] headerLineParts = readLine.split(": ");
            headers.put(headerLineParts[0], headerLineParts[1]);
        }

        log.info("[Request] header: {}", headers);
        return new RequestHeader(headers);
    }

    private RequestBody extractRequestBody(BufferedReader reader, RequestHeader header) throws IOException {
        try {
            int contentLength = Integer.parseInt(header.getHeader("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new RequestBody(new String(buffer));

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
