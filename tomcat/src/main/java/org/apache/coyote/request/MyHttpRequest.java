package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;

public record MyHttpRequest(
        String method,
        String uri,
        String resourcePath,
        String contentType,
        String version
) {

    private static final String RESOURCE_PATH_PREFIX = "static";

    public MyHttpRequest {
        resourcePath = RESOURCE_PATH_PREFIX + resolveResourcePath(resourcePath);
    }

    public static MyHttpRequest of(String rawRequest) {
        String requestLine = extractRequestLine(rawRequest);
        String[] split = requestLine.split(" ");
        return new MyHttpRequest(
                split[0],
                split[1],
                split[1].substring(
                        split[1].indexOf("/"),
                        split[1].lastIndexOf("?") == -1 ? split[1].length() : split[1].lastIndexOf("?")
                ),
                contentTypeOf(split[1]),
                split[2]
        );
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

    private static String contentTypeOf(String url) {
        int lastDotIndex = url.lastIndexOf(".");
        String fileNameExtension = url.substring(lastDotIndex + 1);
        return switch (fileNameExtension) {
            case "/", "html" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "text/javascript";
            case "ico" -> "image/x-icon";
            default -> "text/html";
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
