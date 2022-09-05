package org.apache.coyote.utils;

import org.apache.coyote.exception.NotFoundFileException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.model.ContentType.HTML;

public class RequestUtil {

    public static final String DEFAULT_EXTENSION = ".html";
    public static final String DEFAULT_INDEX = "/";
    public static final String STATIC = "static";
    public static final String EXTENSION_SEPARATOR = ".";
    public static final String PARAM_START_SEPARATOR = "?";
    public static final String PARAM_COUPLER = "=";
    public static final String PARAM_DELIMITER = "&";
    public static final int KEY = 0;
    public static final int VALUE = 1;

    public static String getResponseBody(final String uri, final Class<?> ClassType) {
        try {
            if (uri.equals(DEFAULT_INDEX)) {
                return "Hello world!";
            }
            final URL url = Objects.requireNonNull(ClassType.getClassLoader().getResource(STATIC + uri));
            final Path path = Paths.get(url.toURI());
            return new String(Files.readAllBytes(path));
        } catch (Exception e) {
            throw new NotFoundFileException("파일 찾기에 실패했습니다.");
        }
    }

    public static String getExtension(final String uri) {
        Objects.requireNonNull(uri);
        if (uri.contains(EXTENSION_SEPARATOR)) {
            return uri.substring(uri.lastIndexOf(EXTENSION_SEPARATOR) + 1);
        }
        return HTML.getExtension();
    }

    public static String calculatePath(final String uri) {
        Objects.requireNonNull(uri);
        String path = uri;
        if (path.contains(PARAM_START_SEPARATOR)) {
            path = path.substring(0, uri.indexOf(PARAM_START_SEPARATOR));
        }
        if (!path.contains(EXTENSION_SEPARATOR) && !path.equals(DEFAULT_INDEX)) {
            path += DEFAULT_EXTENSION;
        }
        return path;
    }

    public static Map<String, String> getParam(final String uri) {
        Objects.requireNonNull(uri);
        if (!uri.contains(PARAM_START_SEPARATOR)) {
            return Collections.emptyMap();
        }
        return calculateParam(uri);
    }

    private static Map<String, String> calculateParam(final String uri) {
        List<String> inputs = Arrays.asList(uri.substring(uri.indexOf(PARAM_START_SEPARATOR) + 1).split(PARAM_DELIMITER));
        final Map<String, String> queryParams = new HashMap<>();
        for (String input : inputs) {
            List<String> query = Arrays.asList(input.split(PARAM_COUPLER));
            queryParams.put(query.get(KEY), query.get(VALUE));
        }
        return queryParams;
    }
}
