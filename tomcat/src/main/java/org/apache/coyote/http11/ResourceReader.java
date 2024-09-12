package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class ResourceReader {
    private static final ResourceReader INSTANCE = new ResourceReader();
    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
    private static final String FILE_PATH_PREFIX = "static";
    private static final String NOT_FOUND_HTML = FILE_PATH_PREFIX + "/404.html";

    private ResourceReader() {
    }

    public static ResourceReader getInstance() {
        return INSTANCE;
    }

    public String loadResourceAsString(String fileName) throws IOException {
        URL url = getURL(fileName);
        File file = new File(url.getFile());
        Path path = file.toPath();
        StringBuilder resourceContent = new StringBuilder();
        try (BufferedReader htmlBufferedReader = new BufferedReader(new FileReader(path.toString()))) {
            String line;
            while ((line = htmlBufferedReader.readLine()) != null) {
                resourceContent.append(line).append("\n");
            }
        }
        return resourceContent.toString();
    }

    private URL getURL(String fileName) {
        String filePath = getFilePath(fileName);
        URL url = CLASS_LOADER.getResource(filePath);
        if (url == null) {
            return CLASS_LOADER.getResource(NOT_FOUND_HTML);
        }
        return url;
    }

    private String getFilePath(String fileName) {
        if (fileName.endsWith("/")) {
            return FILE_PATH_PREFIX + fileName;
        }
        return FILE_PATH_PREFIX + "/" + fileName;
    }
}
