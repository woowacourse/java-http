package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class StaticResourceProcessor {

    private static final Map<String, String> CONTENT_TYPE_MAP = Map.ofEntries(
            Map.entry(".html", "text/html;charset=utf-8"),
            Map.entry(".css",  "text/css"),
            Map.entry(".js",   "application/javascript")
    );
    
    public static final String STATIC_RESOURCE_PATH = "static";
    public static final String QUERY_PARAM_STARTER = "?";
    private static final String HTML_EXTENSION = ".html";

    public static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length: ";
    public static final String HTTP_LINE_SEPARATOR = "\r\n";

    public static void process(HttpRequest request, OutputStream outputStream) throws IOException {
        String resourcePath = resolveResourcePath(request.getRequestUri());
        String contentType = determineContentType(resourcePath);
        
        try (InputStream inputStream = StaticResourceProcessor.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                HttpResponse notFoundResponse = new HttpResponse("HTTP/1.1", HttpStatus.NOT_FOUND, 
                    Map.of("Content-Type", "text/html;charset=utf-8", "Content-Length", "0"), "");
                sendResponse(outputStream, notFoundResponse);
                return;
            }
            byte[] responseBody = inputStream.readAllBytes();
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", contentType);
            headers.put("Content-Length", String.valueOf(responseBody.length));
            
            HttpResponse response = new HttpResponse("HTTP/1.1", HttpStatus.OK, headers, 
                new String(responseBody, StandardCharsets.UTF_8));
            sendResponse(outputStream, response);
        }
    }

    private static String resolveResourcePath(String requestUri) {
        int queryIndex = requestUri.indexOf(QUERY_PARAM_STARTER);
        if (queryIndex != -1) {
            requestUri = requestUri.substring(0, queryIndex);
        }
        
        if (hasNoExtension(requestUri)) {
            return STATIC_RESOURCE_PATH + requestUri + HTML_EXTENSION;
        }
        return STATIC_RESOURCE_PATH + requestUri;
    }

    private static String determineContentType(String resourceName) {
        int lastDotIndex = resourceName.lastIndexOf('.');
        String fileExtension = "";
        if (lastDotIndex > 0) {
            fileExtension = resourceName.substring(lastDotIndex);
        }
        return CONTENT_TYPE_MAP.getOrDefault(fileExtension, "text/html;charset=utf-8");
    }

    private static boolean hasNoExtension(String resource) {
        int lastDotIndex = resource.lastIndexOf(".");
        return lastDotIndex == -1 || lastDotIndex == 0 || lastDotIndex == resource.length() - 1;
    }

    private static void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toHttpString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
