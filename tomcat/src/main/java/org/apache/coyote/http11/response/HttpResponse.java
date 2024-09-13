package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String DEFAULT_STATUS_CODE = "200 OK";
    private static final String DEFAULT_RESPONSE_BODY = "";
    public static final String FILE_EXTENSION_DELIMITER = ".";

    private Map<String, String> headers = new LinkedHashMap<>();
    private String statusCode = DEFAULT_STATUS_CODE;
    private String body = DEFAULT_RESPONSE_BODY;

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setBodyWithStaticResource(String filePath) {
        try {
            URL resource = getClass().getClassLoader().getResource("static" + filePath);
            if (resource == null) {
                throw new IOException("존재하지 않는 파일입니다. : " + filePath);
            }

            byte[] fileContents = Files.readAllBytes(new File(resource.getFile()).toPath());
            String fileExtension = getFileExtension(filePath);

            setBodyWithContentType(new String(fileContents), "text/" + fileExtension + ";charset=utf-8");
        } catch (IOException e) {
            this.statusCode = "500 Internal Server Error";
        }
    }

    public void setBodyWithContentType(String body, String contentType) {
        this.body = body;
        this.headers.put("Content-Type", contentType);
        this.headers.put("Content-Length", String.valueOf(body.getBytes().length));
    }

    private String getFileExtension(String filePath) {
        String fileExtension = "html";
        int fileExtensionIndex = filePath.lastIndexOf(FILE_EXTENSION_DELIMITER);
        if (fileExtensionIndex >= 0) {
            fileExtension = filePath.substring(fileExtensionIndex)
                    .replace(FILE_EXTENSION_DELIMITER, "");
        }
        return fileExtension;
    }

    @Override
    public String toString() {
        String statusLine = "HTTP/1.1 " + this.statusCode + " ";
        StringBuilder headerBuilder = new StringBuilder();
        headers.entrySet()
                .stream()
                .forEach(entry -> headerBuilder.append(String.format("%s: %s \r\n", entry.getKey(), entry.getValue())));

        return String.join("\r\n", statusLine, headerBuilder, body);
    }
}
