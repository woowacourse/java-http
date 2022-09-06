package org.apache.coyote.http11.response.generator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.exception.ResourceNotFoundException;

public abstract class FileResponseGenerator implements ResponseGenerator {

    private static final String ROOT_PAGE_REQUEST_URI = "/";
    private static final String ROOT_PAGE = "Hello world!";
    private static final String FILE_EXTENSION_PREFIX = ".";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String HTML_FILE_EXTENSION = ".html";

    protected final String generate(String resource) throws IOException {
        if (resource.equals(ROOT_PAGE_REQUEST_URI)) {
            return ROOT_PAGE;
        }
        return generateResponseBodyByFile(resource);
    }

    private String generateResponseBodyByFile(String resource) throws IOException {
        String resourceName = getResourceName(resource);
        URL url = getClass().getClassLoader()
                .getResource(resourceName);
        checkNotFoundResource(url);
        Path path = new File(url.getFile()).toPath();
        return Files.readString(path);
    }

    private void checkNotFoundResource(URL url) {
        if (url == null) {
            throw new ResourceNotFoundException();
        }
    }

    private String getResourceName(String resource) {
        if (!resource.contains(FILE_EXTENSION_PREFIX)) {
            return STATIC_RESOURCE_PATH + resource + HTML_FILE_EXTENSION;
        }
        return STATIC_RESOURCE_PATH + resource;
    }
}
