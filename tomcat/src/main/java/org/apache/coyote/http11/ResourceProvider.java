package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

public class ResourceProvider {

    public boolean haveResource(String resourcePath) {
        return findFileURL(resourcePath).isPresent();
    }

    public String resourceBodyOf(String resourcePath) {
        try {
            Optional<URL> fileURL = findFileURL(resourcePath);
            if (fileURL.isPresent()) {
                URL url = fileURL.get();
                return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
            }
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        } catch (IOException e) {
            return null;
        }
    }

    private Optional<URL> findFileURL(String resourcePath) {
        URL resourceURL = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resourceURL == null) {
            return Optional.empty();
        }
        if (new File(resourceURL.getFile()).isDirectory()) {
            return Optional.empty();
        }
        return Optional.of(resourceURL);
    }

    public String contentTypeOf(String resourcePath) {
        File file = getFile(resourcePath);
        String fileName = file.getName();
        if (fileName.endsWith(".js")) {
            return "Content-Type: text/javascript ";
        }
        if (fileName.endsWith(".css")) {
            return "Content-Type: text/css ";
        }
        if (fileName.endsWith(".html")) {
            return "Content-Type: text/html;charset=utf-8 ";
        }
        return "text/plain";
    }

    private File getFile(String resourcePath) {
        return new File(
            findFileURL(resourcePath).orElseThrow((() -> new IllegalArgumentException("파일이 존재하지 않습니다.")))
                .getFile());
    }

    public String staticResourceResponse(String resourcePath) {
        String responseBody = resourceBodyOf(resourcePath);
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            contentTypeOf(resourcePath),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
