package org.apache.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UrlUtil {
    public static String joinUrl(String... paths) {
        return Arrays.stream(paths)
                .map(path -> path.replaceAll("/+", ""))
                .collect(Collectors.joining("/"));
    }
}
