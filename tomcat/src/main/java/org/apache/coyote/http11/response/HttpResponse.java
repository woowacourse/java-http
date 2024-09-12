package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private String statusCode = "200 OK";
    private Map<String, String> headers = new LinkedHashMap<>();
    private String body = "";

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBodyWithContentType(String body, String contentType) {
        this.body = body;
        this.headers.put("Content-Type", contentType);
        this.headers.put("Content-Length", String.valueOf(body.getBytes().length));
    }

    public void setBodyWithStaticResource(String filePath) {
        try {
            URL resource = getClass().getClassLoader().getResource("static" + filePath);
            if (resource == null) {
                throw new IOException("존재하지 않는 파일입니다. : " + filePath);
            }
            byte[] fileContents = Files.readAllBytes(new File(resource.getFile()).toPath());

            String fileExtension = "html";
            int fileExtensionIndex = filePath.lastIndexOf(".");
            if (fileExtensionIndex >= 0) {
                fileExtension = filePath.substring(fileExtensionIndex)
                        .replace(".", "");
            }

            setBodyWithContentType(
                    new String(fileContents),
                    String.format("%s%s%s", "text/", fileExtension, ";charset=utf-8")
            );
        } catch (IOException e) {
            this.statusCode = "500 Internal Server Error";
        }
    }

    @Override
    public String toString() {
        // TODO: HTTP 버전도 request와 통일시키기
        String statusLine = String.format("%s %s ", "HTTP/1.1", this.statusCode);
        StringBuilder headerBuilder = new StringBuilder();
        headers.entrySet()
                .stream()
                .forEach(entry -> headerBuilder.append(String.format("%s: %s \r\n", entry.getKey(), entry.getValue())));

        return String.join("\r\n", statusLine, headerBuilder, body);
    }
}
