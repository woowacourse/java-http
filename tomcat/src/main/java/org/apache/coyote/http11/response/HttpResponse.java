package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private String statusCode = "500 Internal Server Error";
    private Map<String, String> headers = new HashMap<>();
    private String body = "";

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBodyWithStaticResource(String filePath) {
        try {
            URL resource = getClass().getClassLoader().getResource("static" + filePath);
            byte[] fileContents = Files.readAllBytes(new File(resource.getFile()).toPath());

            String fileExtension = "html";
            int fileExtensionIndex = filePath.lastIndexOf(".");
            if (fileExtensionIndex >= 0) {
                fileExtension = filePath.substring(fileExtensionIndex)
                        .replace(".", "");
            }

            this.headers.put("Content-Type", String.format("%s%s%s ", "text/", fileExtension, ";charset=utf-8"));
            this.headers.put("Content-Length", String.format("%s ", body.getBytes().length));
            this.body = new String(fileContents);
        } catch (IOException e) {
        }
    }

    @Override
    public String toString() {
        // TODO: HTTP 버전도 request와 통일시키기
        String statusLine = String.format("%s %s ", "HTTP/1.1", this.statusCode);
        StringBuilder headerBuilder = new StringBuilder();
        headers.entrySet()
                .stream()
                .forEach(entry -> headerBuilder.append(String.format("%s: %s ", entry.getKey(), entry.getValue())));

        return String.join("\r\n", statusLine, headerBuilder, "", body);
    }
}
