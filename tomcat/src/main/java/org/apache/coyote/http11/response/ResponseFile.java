package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.exception.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseFile {
    private static final String RESOURCE_PREFIX = "static/";
    private static final ClassLoader CLASS_LOADER = ResponseFile.class.getClassLoader();
    private static final Logger log = LoggerFactory.getLogger(ResponseFile.class);

    private final String contentType;
    private final String content;

    public ResponseFile(String contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    public static ResponseFile of(String requestPath) {
        URL resource = CLASS_LOADER.getResource(RESOURCE_PREFIX + requestPath);
        if (resource == null) {
            log.warn("존재하지 않는 자원입니다: {}", requestPath);
            throw new FileNotFoundException();
        }

        return createFileResponse(requestPath, resource);
    }

    private static ResponseFile createFileResponse(String requestPath, URL resource) {
        File resourceFile = new File(resource.getFile());
        Path resourceFilePath = resourceFile.toPath();

        try {
            String contentType = Files.probeContentType(resourceFilePath) + ";charset=utf-8";
            String responseBody = new String(Files.readAllBytes(resourceFilePath));
            return new ResponseFile(contentType, responseBody);
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", requestPath);
            throw new IllegalArgumentException("파일 정보를 읽지 못했습니다.");
        }
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public int getContentLength() {
        return content.getBytes().length;
    }
}
