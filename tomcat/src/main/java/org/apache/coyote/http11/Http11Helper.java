package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.techcourse.exception.UncheckedServletException;

public class Http11Helper {
    private static final String DEFAULT_EXTENSION = MimeType.HTML.getExtension();
    private static final Http11Helper instance = new Http11Helper();

    private Http11Helper() {
    }

    public static Http11Helper getInstance() {
        return instance;
    }

    public Map<String, String> parseRequestBody(String body) {
        Map<String, String> bodyParams = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                bodyParams.put(keyValue[0], keyValue[1]);
            }
        }
        return bodyParams;
    }

    public String getFileName(String endpoint) {
        int index = endpoint.indexOf("?");
        String path = endpoint;
        if (index != -1) {
            path = path.substring(0, index);
        }
        String fileName = path.substring(1);
        if (fileName.isEmpty()) {
            fileName = "hello.html";
        }
        if (isWithoutExtension(fileName)) {
            fileName = String.join(".", fileName, DEFAULT_EXTENSION);
        }
        return fileName;
    }

    private boolean isWithoutExtension(String fileName) {
        return !fileName.contains(".");
    }

    public String createResponse(HttpStatus status, String fileName) throws IOException {
        String responseBody = getResponseBody(fileName);
        MimeType mimeType = MimeType.getMimeType(fileName);
        return String.join("\r\n",
                "HTTP/1.1 " + status.getValue() + " ",
                "Content-Type: " + mimeType.getType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getResponseBody(String fileName) throws IOException {
        URL resource = findResource(fileName);
        if (Objects.isNull(resource)) {
            throw new UncheckedServletException("Cannot find resource with name: " + fileName);
        }
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private URL findResource(String fileName) {
        return getClass().getClassLoader().getResource("static/" + fileName);
    }
}
