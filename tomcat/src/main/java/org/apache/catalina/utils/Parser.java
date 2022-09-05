package org.apache.catalina.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    private static final String BLANK_LETTER = " ";
    private static final String EMPTY_LETTER = "";
    private static final String DOT_REGEX = "\\.";
    private static final String DOT_LETTER = ".";
    private static final String QUESTION_MARK_LETTER = "?";
    private static final String AMPERSAND_LETTER = "&";
    private static final String EQUAL_LETTER = "=";

    public static String removeBlank(final String input) {
        return input.replaceAll(BLANK_LETTER, EMPTY_LETTER);
    }

    public static String convertResourceFileName(final String path) {
        try {
            validateFileType(path);
        } catch (IllegalArgumentException exception) {
            return path + DOT_LETTER + FileType.HTML.getValue();
        }
        return path;
    }

    public static String parseFileType(final String fileName) {
        validateFileType(fileName);
        return fileName.split(DOT_REGEX)[1];
    }

    private static void validateFileType(final String fileName) {
        if (!fileName.contains(DOT_LETTER)) {
            throw new IllegalArgumentException("파일 형식이 아닙니다.");
        }
    }

    public static Map<String, String> parseQueryParams(final String path) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        final String query = path.substring(getQueryParameterBeginIndex(path));
        final List<String> params = List.of(query.split(AMPERSAND_LETTER));
        for (String param : params) {
            final List<String> element = List.of(param.split(EQUAL_LETTER));
            queryParams.put(element.get(0), element.get(1));
        }
        return queryParams;
    }

    private static int getQueryParameterBeginIndex(final String path) {
        return path.indexOf(QUESTION_MARK_LETTER) + 1;
    }
}
