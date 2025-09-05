package org.apache.catalina.util;

import com.http.enums.HttpStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileParser {

    private static final Logger log = LoggerFactory.getLogger(FileParser.class);

    private FileParser() {
    }

    public static boolean existsFile(String path) {
        final URL url = getFileUrl(path);
        return url != null;
    }

    public static byte[] loadStaticResource(String path) throws IOException {
        final URL url = getFileUrl(path);

        return Files.readAllBytes(new File(url.getFile()).toPath());
    }

    public static byte[] loadStaticResourceByFileName(String fileName) throws IOException {
        if (!fileName.contains(".")) {
            log.error("잘못된 파일명입니다. fileName : {}", fileName);
            throw new FileNotFoundException("잘못된 파일 명입니다. fileName : " + fileName);
        }

        validatePath(fileName);

        String resourcePath = fileName.startsWith("/") ? fileName.substring(1) : fileName;
        resourcePath = "static/" + resourcePath;

        final URL url = FileParser.class.getClassLoader().getResource(resourcePath);

        if (url == null) {
            log.error("파일이 존재하지 않습니다. fileName : {}", fileName);
            throw new FileNotFoundException("파일이 존재하지 않습니다. fileName : " + fileName);
        }

        return Files.readAllBytes(new File(url.getFile()).toPath());
    }

    private static URL getFileUrl(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }

        validatePath(path);

        String resourcePath = path.startsWith("/") ? path.substring(1) : path;
        resourcePath = "static/" + resourcePath;

        return FileParser.class.getClassLoader().getResource(resourcePath);
    }

    private static void validatePath(String path) {
        if (path.contains("../") || path.contains("..\\")) {
            throw new IllegalArgumentException("Path traversal attempt detected: " + path);
        }

        if (path.contains("..")) {
            throw new IllegalArgumentException("Invalid path format: " + path);
        }
    }

    public static byte[] loadErrorPage(HttpStatus httpStatus) throws IOException {
        String errorPagePath = "error/" + httpStatus.getCode() + ".html";
        
        final URL url = FileParser.class.getClassLoader().getResource(errorPagePath);
        
        if (url == null) {
            log.warn("에러 페이지를 찾을 수 없습니다. 기본 메시지를 반환합니다. errorPagePath: {}", errorPagePath);
            return getDefaultErrorMessage(httpStatus).getBytes();
        }
        
        return Files.readAllBytes(new File(url.getFile()).toPath());
    }
    
    private static String getDefaultErrorMessage(HttpStatus httpStatus) {
        return String.format(
            "<html><body><h1>%d %s</h1></body></html>",
            httpStatus.getCode(),
            httpStatus.getReasonPhrase()
        );
    }
}
