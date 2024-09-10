package org.apache.catalina;

import com.techcourse.http.MimeType;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class StaticResourceProvider {

    private static final ClassLoader CLASS_LOADER = StaticResourceProvider.class.getClassLoader();
    private static final Map<String, String> CACHE = new HashMap<>();
    private static final String STATIC_PATH = "static";
    private static final int MAX_LENGTH = 1024 * 1024;

    static public String getStaticResource(String path) throws IOException {
        if (CACHE.containsKey(path)) {
            return CACHE.get(path);
        }

        String resource = readResource(path);
        if (resource.length() <= MAX_LENGTH) {
            CACHE.put(path, resource);
        }
        return resource;
    }

    static private String readResource(String path) throws IOException {
        URL resource = CLASS_LOADER.getResource(STATIC_PATH + path);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found");
        }
        return new String(Files.readAllBytes(Path.of(resource.getPath())));
    }

    static public String probeContentType(String path) {
        String endPath = path.substring(path.lastIndexOf("/") + 1);
        return MimeType.from(endPath);
    }
}
