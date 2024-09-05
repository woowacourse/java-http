package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ResponseResolver {

    private static final ViewResolver viewResolver = new ViewResolver();

    public static String resolveResponse(Map<String, String> map, String contentType) {
        int statusCode = Integer.parseInt(map.get("statusCode"));

        if (statusCode == 200) {
            String body = findResponseFile(map.get("url"));
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);
        }

        if (statusCode == 302) {
            return String.join("\r\n",
                    "HTTP/1.1 302  ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Location: " + map.get("url"),
                    "");
        }

        return null;
    }

    public static String resolveViewResponse(String url, String contentType) {
        String body = findResponseFile(url);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private static String findResponseFile(String viewUri) {
        try {
            Path path = new ViewResolver().findViewPath(viewUri)
                    .orElseThrow(() -> new IllegalArgumentException(viewUri + "잘못된 viewUri입니다."));
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
