package org.apache.catalina.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public final class FileParser {

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
            throw new IllegalArgumentException("잘못된 파일 명입니다. fileName : " + fileName);
        }

        validatePath(fileName);

        String resourcePath = fileName.startsWith("/") ? fileName.substring(1) : fileName;
        resourcePath = "static/" + resourcePath;

        final URL url = FileParser.class.getClassLoader().getResource(resourcePath);

        if (url == null) {
            return "File not found".getBytes();
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
}
