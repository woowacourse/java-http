package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {

    public HttpRequest parse(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final List<String> httpRequestMessages = getHttpRequestHeaders(reader);
        final String[] requestLine = httpRequestMessages.getFirst().split(" ");
        final var method = parseMethod(requestLine);
        final var path = parsePath(requestLine);
        final var queryParameter = parseQuaryParameter(requestLine);
        final var httpVersion = parseHttpVersion(requestLine);
        final var contentType = parseAccept(httpRequestMessages);
        final var cookies = parseCookies(httpRequestMessages);
        final var contentLength = parseContentLength(httpRequestMessages);

        final Map<String, String> body = new HashMap<>();
        if (contentLength != 0) {
            parseBody(body, httpRequestMessages.getLast());
        }

        return new HttpRequest(
                method,
                path,
                httpVersion,
                contentType,
                contentLength,
                cookies,
                queryParameter,
                body
        );
    }

    private List<HttpCookie> parseCookies(List<String> httpRequestMessages) {
        List<HttpCookie> cookies = new ArrayList<>();
        for (String line : httpRequestMessages) {
            if (line.startsWith("Cookie:")) {
                String cookieHeader = line.substring("Cookie:".length()).trim();
                String[] cookiePairs = cookieHeader.split(";");
                for (String pair : cookiePairs) {
                    String[] keyValue = pair.trim().split("=", 2);
                    if (keyValue.length == 2) {
                        cookies.add(new HttpCookie(keyValue[0], keyValue[1]));
                    }
                }
            }
        }
        return cookies;
    }

    private List<String> getHttpRequestHeaders(BufferedReader reader) {
        final List<String> httpRequestLines = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                httpRequestLines.add(line);
            }
            if (httpRequestLines.isEmpty()) {
                throw new IllegalArgumentException("잘못된 요청입니다.");
            }

            httpRequestLines.add("");

            int contentLength = parseContentLength(httpRequestLines);
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                reader.read(bodyChars, 0, contentLength);
                httpRequestLines.add(new String(bodyChars));
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        return httpRequestLines;
    }

    private int parseContentLength(List<String> headers) {
        for (String line : headers) {
            if (line.startsWith("Content-Length:")) {
                return Integer.parseInt(line.substring("Content-Length:".length()).trim());
            }
        }
        return 0;
    }

    private Method parseMethod(String[] requestLine) {
        return Method.fromHeaderValue(requestLine[0].trim());
    }

    private String parsePath(String[] requestLine) {
        String requestTarget = requestLine[1].trim();
        if (requestTarget.contains("?")) {
            requestTarget = requestTarget.substring(0, requestTarget.indexOf("?"));
        }
        return requestTarget;
    }

    private Map<String, String> parseQuaryParameter(String[] requestLine) {
        final String requestTarget = requestLine[1].trim();
        if (requestTarget.contains("?")) {
            return parseQueryParameterPair(requestTarget);
        }
        return new HashMap<>();
    }

    private Map<String, String> parseQueryParameterPair(String requestTarget) {
        final Map<String, String> queryParameterPair = new HashMap<>();
        final String queryString = requestTarget.substring(requestTarget.indexOf("?") + 1);
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                queryParameterPair.put(keyValue[0], keyValue[1]);
            }
        }
        return queryParameterPair;
    }

    private HttpVersion parseHttpVersion(String[] requestLine) {
        return HttpVersion.fromHeaderValue(requestLine[2].trim());
    }

    private ContentType parseAccept(List<String> httpRequestMessages) {
        for (String line : httpRequestMessages) {
            if (line.startsWith("Accept:")) {
                String acceptValue = line.substring(("Accept: ").length()).trim();
                int semicolonIndex = acceptValue.indexOf(',');
                if (semicolonIndex != -1) {
                    acceptValue = acceptValue.substring(0, semicolonIndex).trim();
                }
                return ContentType.fromHeaderValue(acceptValue.trim());
            }
        }
        return ContentType.ALL;
    }

    private Map<String, String> parseBody(Map<String, String> body, String httpRequestBody) {
        for (String pair : httpRequestBody.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                body.put(keyValue[0], keyValue[1]);
            }
        }
        return body;
    }
}
