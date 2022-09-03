package org.apache.catalina.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    public static String removeBlank(final String input) {
        return input.replaceAll(" ", "");
    }

    public static String convertResourceFileName(final String path) {
        try {
            validateFileType(path);
        } catch (IllegalArgumentException exception) {
            return path + "." + FileType.HTML.getValue();
        }
        return path;
    }

    public static String parseFileType(final String fileName) {
        validateFileType(fileName);
        return fileName.split("\\.")[1];
    }

    private static void validateFileType(final String fileName) {
        if (!fileName.contains(".")) {
            throw new IllegalArgumentException("파일 형식이 아닙니다.");
        }
    }

    public static Map<String, String> parseQueryParams(final String path) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        final String query = path.substring(path.indexOf("?") + 1);
        final List<String> params = List.of(query.split("&"));
        for (String param : params) {
            final List<String> element = List.of(param.split("="));
            queryParams.put(element.get(0), element.get(1));
        }
        return queryParams;
    }
}
