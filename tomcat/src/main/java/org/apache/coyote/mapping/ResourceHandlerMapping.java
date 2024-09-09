package org.apache.coyote.mapping;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.http.response.HttpResponseGenerator;

public class ResourceHandlerMapping {

    private static final Map<String, String> STATIC_RESOURCE_EXTENSIONS = Map.of(
            "css", "css",
            "js", "assets"
    );
    private static final String STATIC_RESOURCE_ROOT_PATH = "static/";
    private static final String PATH_DELIMITER = "/";

    private static final ResourceHandlerMapping INSTANCE = new ResourceHandlerMapping();

    private ResourceHandlerMapping() {
    }

    public static ResourceHandlerMapping getInstance() {
        return INSTANCE;
    }

    public String handleSimpleResource(final String resourceName) {
        final URL resourceURL = getClass().getClassLoader().getResource(findResourcePath(resourceName));
        final Path resourcePath = Path.of(resourceURL.getPath());
        try {
            final String responseBody = Files.readString(resourcePath);
            final String mimeType = Files.probeContentType(resourcePath);
            return HttpResponseGenerator.getOkResponse(mimeType, responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return HttpResponseGenerator.getFoundResponse("404.html");
        }
    }

    private String findResourcePath(final String resourcePath) {
        final String[] fileNames = resourcePath.split("\\.");
        final String extension = fileNames[1];

        if (STATIC_RESOURCE_EXTENSIONS.containsKey(extension)) {
            return STATIC_RESOURCE_ROOT_PATH
                    .concat(STATIC_RESOURCE_EXTENSIONS.get(extension))
                    .concat(PATH_DELIMITER)
                    .concat(resourcePath);
        }

        return STATIC_RESOURCE_ROOT_PATH.concat(resourcePath);
    }
}
