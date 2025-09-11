package org.apache.coyote.http11.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static Optional<HttpRequest> parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = br.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            return Optional.empty();
        }

        String[] requestLineInfo = requestLine.split(" ");
        if (requestLineInfo.length < 2) {
            return Optional.empty();
        }

        String httpMethod = requestLineInfo[0];
        String fullUrl = requestLineInfo[1];
        String url = parseUrl(fullUrl);

        Map<String, String> headers = new HashMap<>();
        List<HttpCookie> cookies = new ArrayList<>();
        Map<String, String> parameters = new HashMap<>();

        String rawBody = parseHeadersAndBody(br, headers, cookies, httpMethod, fullUrl, parameters);

        return Optional.of(new HttpRequest(httpMethod, url, headers, cookies, parameters, rawBody));
    }

    private static String parseUrl(String fullUrl) {
        if (fullUrl.contains("?")) {
            return fullUrl.split("\\?")[0];
        }
        return fullUrl;
    }

    private static String parseHeadersAndBody(
            BufferedReader br,
            Map<String, String> headers,
            List<HttpCookie> cookies,
            String httpMethod,
            String fullUrl,
            Map<String, String> parameters
    ) throws IOException {
        String line;
        int contentLength = 0;

        while ((line = br.readLine()) != null && !line.isBlank()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                String headerName = headerParts[0].trim();
                String headerValue = headerParts[1].trim();
                headers.put(headerName, headerValue);

                if (headerName.equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(headerValue);
                }
                if (headerName.equalsIgnoreCase("Cookie")) {
                    cookies.addAll(parseCookies(headerValue));
                }
            }
        }

        String rawBodyContent = null;
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            rawBodyContent = new String(bodyChars);
        }

        if (httpMethod.equalsIgnoreCase("GET") && fullUrl.contains("?")) {
            parseParameters(fullUrl.substring(fullUrl.indexOf("?") + 1), parameters);
        } else if (httpMethod.equalsIgnoreCase("POST") && rawBodyContent != null) {
            parseParameters(rawBodyContent, parameters);
        }

        return rawBodyContent;
    }

    private static List<HttpCookie> parseCookies(String cookieString) {
        List<HttpCookie> cookies = new ArrayList<>();
        String[] cookiePairs = cookieString.split("; ");
        for (String pair : cookiePairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String name = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                cookies.add(new HttpCookie(name, value));
            }
        }
        return cookies;
    }

    private static void parseParameters(String data, Map<String, String> parameters) {
        if (data == null || data.isBlank()) {
            return;
        }
        String[] params = data.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                parameters.put(key, value);
            }
        }
    }
}