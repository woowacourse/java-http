package jakarta.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;

public class ResourceFinder {

    private static final String RESOURCE_PATH_FORMAT = "static/%s";
    private static final String INDEX_PATH = "/";

    private final ClassLoader classLoader = getClass().getClassLoader();

    public boolean hasResource(String resourceName) {
        URL resourcePath = classLoader.getResource(String.format(RESOURCE_PATH_FORMAT, resourceName));

        return resourcePath != null && !INDEX_PATH.equals(resourceName);
    }

    public byte[] readResource(String resourceName) {
        URL resourcePath = classLoader.getResource(String.format(RESOURCE_PATH_FORMAT, resourceName));
        if (resourcePath == null) {
            throw new NoSuchElementException("존재하지 않는 리소스입니다.");
        }

        try (InputStream inputStream = new FileInputStream(resourcePath.getPath())) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
