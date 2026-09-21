package org.apache.coyote;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.http11.BadRequestException;

public record HttpRequest(
        String method,
        String path,
        String version,
        Map<String, String> headers,
        Map<String, String> queryParameters,
        Map<String, String> formParameters,
        String body,
        HttpCookie cookies,
        Session session
) {

    public HttpRequest {
        headers = Map.copyOf(headers);
        queryParameters = Map.copyOf(queryParameters);
        formParameters = Map.copyOf(formParameters);
    }

    public HttpRequest withSession(Session session) {
        return new HttpRequest(method, path, version, headers, queryParameters, formParameters, body, cookies, session);
    }

    public static Map<String, String> parseHeaders(List<String> headerLines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : headerLines) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                headers.put(parts[0].trim().toLowerCase(Locale.ROOT), parts[1].trim());
            }
        }
        return Map.copyOf(headers);
    }

    public static HttpRequest parse(String requestLine, Map<String, String> headers, byte[] bodyBytes) {
        String[] parts = requestLine.split(" ",3);

        if (parts.length != 3) {
            throw new BadRequestException("Invalid request line: " + requestLine);
        }

        URI uri = createUri(parts[1], requestLine);

        String body = new String(bodyBytes, StandardCharsets.UTF_8);

        Map<String, String> queryParameters = parseParameters(uri.getRawQuery());
        Map<String, String> formParameters = isFormUrlEncoded(headers.get("content-type"))
                ? parseParameters(body)
                : Map.of();

        return new HttpRequest(
                parts[0],
                uri.getPath(),
                parts[2],
                headers,
                queryParameters,
                formParameters,
                body,
                parseCookies(headers.get("cookie")),
                null
        );
    }

    private static boolean isFormUrlEncoded(String contentType) {
        return contentType != null
                && contentType.split(";", 2)[0].trim()
                        .equalsIgnoreCase("application/x-www-form-urlencoded");
    }

    private static HttpCookie parseCookies(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(cookies);
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2 && !parts[0].isBlank()) {
                cookies.put(parts[0].trim(), parts[1].trim());
            }
        }
        return new HttpCookie(cookies);
    }

    private static URI createUri(String target, String requestLine) {
        try {
            return URI.create(target);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid request line: " + requestLine);
        }
    }

    private static Map<String, String> parseParameters(String rawQuery) {
        Map<String, String> query = new HashMap<>();

        if (rawQuery == null || rawQuery.isBlank()) {
            return query;
        }

        for (String parameter : rawQuery.split("&")) {
            addQueryParameter(query, parameter);
        }

        return query;
    }

    private static void addQueryParameter(Map<String, String> query, String parameter) {
        String[] keyValue = parameter.split("=", 2);
        String key = decodeParameter(keyValue[0]);
        String value = "";

        if (keyValue.length == 2) {
            value = decodeParameter(keyValue[1]);
        }

        query.put(key, value);
    }

    private static String decodeParameter(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid URL encoding");
        }
    }

}
