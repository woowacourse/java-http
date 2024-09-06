package org.apache.catalina.response;

import org.apache.catalina.ResourceResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResponseCreator {

    private final ResourceResolver resourceResolver;

    public ResponseCreator() {
        this.resourceResolver = new ResourceResolver();
    }

    public String create(int statusCode, String path) throws IOException {
        String resource = resourceResolver.resolve(path);
        String responseBody = readFile(resource);
        return String.join("\r\n",
                String.format("HTTP/1.1 %d OK ", statusCode),
                "Content-Type: " + getContentType(resource),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String create(int statusCode, String path, String cookie) throws IOException {
        String resource = resourceResolver.resolve(path);
        String responseBody = readFile(resource);
        return String.join("\r\n",
                String.format("HTTP/1.1 %d OK ", statusCode),
                "Content-Type: " + getContentType(resource),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "Set-Cookie: " + cookie + " ",
                "Location: " + path + " ",
                "",
                responseBody);
    }

    private String readFile(String resource) throws IOException {
        File file = new File(resource);
        if (!file.isFile()) {
            return "Hello world!";
        }
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }

    private String getContentType(String contentType) {
        if (contentType.endsWith(".css")) {
            return "text/css ";
        }
        return "text/html;charset=utf-8 ";
    }
}
