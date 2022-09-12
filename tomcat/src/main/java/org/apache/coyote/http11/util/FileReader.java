package org.apache.coyote.http11.util;

import static org.apache.coyote.http11.response.StatusCode.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.coyote.http11.response.HttpResponse;

public class FileReader {
    public FileReader() {
    }

    public HttpResponse readFile(String filePath, String httpVersion) {
        try {
            Path path = Paths.get(getClass()
                .getClassLoader()
                .getResource("static" + filePath)
                .toURI());

            String responseBody = new String(Files.readAllBytes(path));
            String contentType = Files.probeContentType(path);
            String contentLength = Integer.toString(responseBody.getBytes().length);

            return HttpResponse.of(httpVersion, OK, responseBody)
                .addHeader("Content-Type", contentType)
                .addHeader("Content-Length", contentLength);
        } catch (Exception e) {
            throw new IllegalArgumentException("파일을 읽는 과정에서 오류가 발생하였습니다.");
        }
    }
}
