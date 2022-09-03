package org.apache.catalina.utils;

public class Parser {
    public static String removeBlank(final String input) {
        return input.replaceAll(" ", "");
    }

    public static String parseFileType(final String fileName) {
        if (!fileName.contains(".")) {
            throw new IllegalArgumentException("파일이름이 아닙니다. " + fileName);
        }
        return fileName.split("\\.")[1];
    }

    public static String generateResourceFileName(final String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }
}
