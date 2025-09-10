package org.apache.coyote.http11.parser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.http11.HttpRequest;

public class HttpRequestParser {

    private HttpRequestParser() {

    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = br.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            return null;
        }

        String[] requestLineInfo = requestLine.split(" ");
        String httpMethod = requestLineInfo[0];
        String url = requestLineInfo[1];

        Map<String, String> headers = new HashMap<>();
        String cookieHeader = parseHeaders(br, headers);

        int contentLength = 0;
        if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
        }

        String body = parseBody(br, contentLength);
        HttpCookie cookie = new HttpCookie(cookieHeader);

        return new HttpRequest(httpMethod, url, headers, cookie, body);
    }

    private static String parseHeaders(BufferedReader br, Map<String, String> headers) throws IOException {
        String cookieHeader = "";
        String line;
        while ((line = br.readLine()) != null && !line.isBlank()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                String headerName = headerParts[0].trim();
                String headerValue = headerParts[1].trim();
                headers.put(headerName, headerValue);
                if (headerName.equalsIgnoreCase("Cookie")) {
                    cookieHeader = headerValue;
                }
            }
        }
        return cookieHeader;
    }

    private static String parseBody(BufferedReader br, int contentLength) throws IOException {
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            return new String(bodyChars);
        }
        return "";
    }
}
