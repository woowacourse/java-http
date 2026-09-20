package org.apache.coyote.request;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.cookie.HttpCookie;

import java.util.HashMap;
import java.util.Map;

public record MyHttpRequest(
        String method,
        String uri,
        String resourcePath,
        ContentType contentType,
        String version,
        HttpCookie cookie,
        String body
) {

    private static final String RESOURCE_PATH_PREFIX = "static";

    public MyHttpRequest {
        resourcePath = RESOURCE_PATH_PREFIX + resolveResourcePath(resourcePath);
    }

    public static MyHttpRequest of(String rawRequest) {
        String requestLine = extractRequestLine(rawRequest);
        String[] split = requestLine.split(" ");

        if (split.length != 3) {
            throw new IllegalArgumentException("잘못된 Http request 입니다: " + requestLine);
        }

        return new MyHttpRequest(
                split[0],
                split[1],
                extractResourcePath(split[1]),
                contentTypeOf(split[1]),
                split[2],
                extractCookie(rawRequest),
                extractBody(rawRequest)
        );
    }

    private static HttpCookie extractCookie(String rawRequest) {
        return rawRequest.lines()
                .filter(line -> line.startsWith("Cookie: "))
                .map(line -> line.substring("Cookie: ".length()))
                .map(HttpCookie::from)
                .findFirst()
                .orElse(HttpCookie.from(""));
    }

    private static String extractBody(String rawRequest) {
        final String bodySeparator = "\r\n\r\n";
        int startIndexOfBody = rawRequest.indexOf(bodySeparator);
        if (startIndexOfBody == -1) {
            return "";
        }
        return rawRequest.substring(startIndexOfBody + bodySeparator.length());
    }

    public boolean hasCookie(String cookieKeyName) {
        return cookie.has(cookieKeyName);
    }

    public boolean hasRequestBody() {
        return !body.isEmpty();
    }

    public boolean hasQueryParameter() {
        return uri.contains("?");
    }

    public Map<String, String> queryParameters() {
        Map<String, String> params = new HashMap<>();
        String queryParams = uri.split("\\?")[1];
        for (String queryParam : queryParams.split("&")) {
            String[] keyValue = queryParam.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    private static String extractRequestLine(String rawRequest) {
        return rawRequest.lines()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("http 요청을 읽을 수 없습니다."));
    }

    private static String extractResourcePath(String uri) {
        int startIndexOfPath = uri.indexOf("/");
        int startIndexOfQueryString = uri.indexOf("?");

        if (startIndexOfQueryString == -1) {
            return uri.substring(startIndexOfPath);
        }
        return uri.substring(startIndexOfPath, startIndexOfQueryString);
    }

    private static ContentType contentTypeOf(String url) {
        int lastDotIndex = url.lastIndexOf(".");
        String fileNameExtension = url.substring(lastDotIndex + 1);
        return switch (fileNameExtension) {
            case "/", "html" -> ContentType.HTML;
            case "css" -> ContentType.CSS;
            case "js" -> ContentType.JAVASCRIPT;
            case "ico" -> ContentType.ICO;
            default -> ContentType.HTML;
        };
    }

    private String resolveResourcePath(String resourcePath) {
        if (isRootPath(resourcePath) || hasExtension(resourcePath)) {
            return resourcePath;
        }
        return resourcePath + ".html";
    }

    private static boolean isRootPath(String resourcePath) {
        return resourcePath.equals("/");
    }

    private boolean hasExtension(String resourcePath) {
        String fileName = resourcePath.substring(resourcePath.lastIndexOf("/") + 1);
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0
                && dotIndex != fileName.length() - 1;
    }
}
