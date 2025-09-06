package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.RequestCookie;
import org.apache.catalina.SessionManager;

public class Http11InputBuffer {

    public static HttpRequest parseToRequest(BufferedReader bufferedReader, SessionManager sessionManager)
            throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }

        String[] splitRequestLine = requestLine.split(" ");
        String httpMethod = splitRequestLine[0];
        String uri = splitRequestLine[1];
        double httpVersion = Double.parseDouble(splitRequestLine[2].split("/")[1]);

        Map<String, String> headers = parseHeaders(bufferedReader);

        String host = headers.getOrDefault("host", "");
        String contentType = headers.getOrDefault("content-type", "");
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        String rawCookie = headers.getOrDefault("cookie", "");

        String requestBody = null;
        if (httpMethod.equals("POST") && contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            bufferedReader.read(bodyChars, 0, contentLength);
            requestBody = new String(bodyChars);
        }

        RequestCookie requestCookie = null;
        if (!rawCookie.isEmpty()) {
            requestCookie = parseToCookie(rawCookie);
        }

        return new HttpRequest(
                sessionManager,
                httpMethod,
                uri,
                httpVersion,
                host,
                contentType,
                requestBody,
                requestCookie
        );
    }

    private static Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).toLowerCase().trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private static RequestCookie parseToCookie(String rawCookies) {
        Map<String, String> cookieValues = new HashMap<>();

        String[] pairs = rawCookies.split("; ");
        for (String pair : pairs) {
            String[] splitPair = pair.split("=");
            String key = splitPair[0];
            String value = splitPair[1];
            cookieValues.put(key, value);
        }

        return new RequestCookie(cookieValues);
    }
}
