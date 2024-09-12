package org.apache.catalina.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.exception.FileException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class File {
    private static final String RESOURCE_PREFIX = "static/";
    private static final String HTML_SUFFIX = ".html";
    private static final String CONTENT_TYPE_SUFFIX = ";charset=utf-8";
    private static final ClassLoader CLASS_LOADER = File.class.getClassLoader();
    private static final Logger log = LoggerFactory.getLogger(File.class);

    private final String contentType;
    private final String content;

    public File(String contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    public static File createHtml(String requestPath) {
        return File.of(requestPath + HTML_SUFFIX);
    }

    public static File of(String requestPath) {
        URL resource = CLASS_LOADER.getResource(RESOURCE_PREFIX + requestPath);
        if (resource == null) {
            log.warn("존재하지 않는 자원입니다: {}", requestPath);
            throw new FileException("존재하지 않는 파일입니다.");
        }

        return createFileResponse(requestPath, resource);
    }

    private static File createFileResponse(String requestPath, URL resource) {
        java.io.File resourceFile = new java.io.File(resource.getFile());
        Path resourceFilePath = resourceFile.toPath();

        try {
            String contentType = Files.probeContentType(resourceFilePath) + CONTENT_TYPE_SUFFIX;
            String responseBody = new String(Files.readAllBytes(resourceFilePath));
            return new File(contentType, responseBody);
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", requestPath);
            throw new FileException("파일 정보를 읽지 못했습니다.");
        }
    }

    public void addToResponse(HttpResponse response) {
        response.setBody(contentType, content);
        response.setHttpStatus(HttpStatus.OK);
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
