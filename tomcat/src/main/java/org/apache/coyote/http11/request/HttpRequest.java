package org.apache.coyote.http11.request;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpVersion;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record HttpRequest(
        HttpMethod method,
        String url,
        Map<String, String> queryParams,
        HttpVersion version,
        Map<String, String> headers,
        String body
) {

    public static HttpRequest fromInputStream(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ByteArrayOutputStream headerStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[8192];
        int bytesRead;
        int headerEndIndex = -1;

        while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
            headerStream.write(buffer, 0, bytesRead);
            String headerString = headerStream.toString(StandardCharsets.UTF_8);
            int index = headerString.indexOf("\r\n\r\n");
            if (index != -1) {
                headerEndIndex = index;
                break;
            }
        }

        if (headerEndIndex == -1) {
            return null; // 헤더가 완전히 읽히지 않음 (잘못된 요청임)
        }

        byte[] fullStreamBytes = headerStream.toByteArray();
        byte[] headerBytes = new byte[headerEndIndex]; // \r\n\r\n 제외
        System.arraycopy(fullStreamBytes, 0, headerBytes, 0, headerEndIndex);

        String headerString = new String(headerBytes, StandardCharsets.US_ASCII);

        String[] lines = headerString.split("\r\n");
        String requestLine = lines[0];

        Pattern pattern = Pattern.compile("(\\w+)\\s+([^\\s]+)\\s+(HTTP/\\d.\\d)");
        Matcher matcher = pattern.matcher(requestLine);

        HttpMethod method = null;
        String url = null;
        HttpVersion version = null;
        Map<String, String> queryParams = new HashMap<>();
        if (matcher.find()) {
            method = HttpMethod.valueOf(matcher.group(1));
            version = HttpVersion.fromString(matcher.group(3));
            String fullUriString = matcher.group(2);
            URI uri;
            try {
                uri = new URI(fullUriString);
            } catch (Exception e) {
                return null;
            }

            url = uri.getPath();
            String query = uri.getQuery();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (idx > 0) {
                        String key = pair.substring(0, idx);
                        String value = pair.substring(idx + 1);
                        queryParams.put(
                                URLDecoder.decode(key, StandardCharsets.UTF_8),
                                URLDecoder.decode(value, StandardCharsets.UTF_8)
                        );
                    }
                }
            }
        }

        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String[] headerParts = lines[i].split(":", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        String body = "";
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            if (contentLength > 0) {
                byte[] bodyBytes = new byte[contentLength];
                int totalBytesRead = 0;

                int remainingBytesInHeaderStream = fullStreamBytes.length - (headerEndIndex + 4);
                if (remainingBytesInHeaderStream > 0) {
                    int bytesToCopy = Math.min(remainingBytesInHeaderStream, contentLength);
                    System.arraycopy(fullStreamBytes, headerEndIndex + 4, bodyBytes, 0, bytesToCopy);
                    totalBytesRead += bytesToCopy;
                }

                while (totalBytesRead < contentLength &&
                        (bytesRead = bufferedInputStream.read(
                                bodyBytes, totalBytesRead,
                                contentLength - totalBytesRead
                        )) != -1) {
                    totalBytesRead += bytesRead;
                }

                body = new String(bodyBytes, 0, totalBytesRead, StandardCharsets.UTF_8);

                if (headers.getOrDefault("Content-Type", "").contains("application/x-www-form-urlencoded")) {
                    body = URLDecoder.decode(body, StandardCharsets.UTF_8);
                }
            }
        }

        return new HttpRequest(method, url, queryParams, version, Collections.unmodifiableMap(headers), body);
    }

    public String getCookie(String key) {
        String cookieHeader = headers.get("Cookie");
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                String[] cookiePair = cookie.trim().split("=");
                if (cookiePair.length == 2 && cookiePair[0].equals(key)) {
                    return cookiePair[1];
                }
            }
        }
        return null;
    }
}
