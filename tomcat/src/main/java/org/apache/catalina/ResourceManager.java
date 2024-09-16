package org.apache.catalina;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceManager {

    private static final String FILE_RESOURCE_PREFIX = "static";

    private ResourceManager() {
    }

    public static String getFileResource(String path) throws IOException {
        URL resource = ResourceManager.class.getClassLoader().getResource(FILE_RESOURCE_PREFIX + path);
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
