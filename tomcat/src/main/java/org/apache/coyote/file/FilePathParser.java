package org.apache.coyote.file;

import java.util.Map;

public class FilePathParser {

    private static final Map<String, String> STATIC_RESOURCE_EXTENSIONS = Map.of(
            "html", "",
            "css", "/css",
            "js", "/assets"
    );
    private static final String STATIC_RESOURCE_ROOT_PATH = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String PATH_DELIMITER = "/";
    private static final String EXTENSION_DELIMITER = "\\.";

    public static String findResourcePath(final String path) {
        final String[] resourceNames = findResourceName(path).split(EXTENSION_DELIMITER);
        final String extension = resourceNames[resourceNames.length - 1];

        if (STATIC_RESOURCE_EXTENSIONS.containsKey(extension)) {
            return STATIC_RESOURCE_ROOT_PATH
                    .concat(path);
        }

        return STATIC_RESOURCE_ROOT_PATH
                .concat(path)
                .concat(DEFAULT_EXTENSION);
    }

    private static String findResourceName(final String path) {
        final String[] paths = path.split(PATH_DELIMITER);
        return paths[paths.length - 1];
    }
}
