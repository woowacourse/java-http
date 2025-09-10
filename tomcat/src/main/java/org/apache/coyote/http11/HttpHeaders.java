package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    
    private static final Map<String, String> CONTENT_TYPE_MAP = Map.ofEntries(
            Map.entry(".html", "text/html;charset=utf-8"),
            Map.entry(".css",  "text/css"),
            Map.entry(".js",   "application/javascript"),
            Map.entry(".svg",  "image/svg+xml")
    );
    
    private final Map<String, String> headers;
    
    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders add(String name, String value) {
        headers.put(name, value);
        return this;
    }
    
    public String get(String name) {
        return headers.get(name);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey())
              .append(": ")
              .append(entry.getValue())
              .append("\r\n");
        }
        return sb.toString();
    }

    public static HttpHeaders html() {
        return new HttpHeaders().add("Content-Type", "text/html;charset=utf-8");
    }
    
    public static HttpHeaders redirect(String location) {
        return new HttpHeaders()
            .add("Location", location)
            .add("Content-Length", "0");
    }
    
    public static HttpHeaders empty() {
        return new HttpHeaders()
            .add("Content-Type", "text/html;charset=utf-8")
            .add("Content-Length", "0");
    }

    public static String getContentType(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        String fileExtension = "";
        if (lastDotIndex > 0) {
            fileExtension = fileName.substring(lastDotIndex);
        }
        return CONTENT_TYPE_MAP.getOrDefault(fileExtension, "text/html;charset=utf-8");
    }

    public static HttpHeaders fromFile(String fileName) {
        return new HttpHeaders().add("Content-Type", getContentType(fileName));
    }
}
